package kmjblog.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kmjblog.domain.ApiResponse;
import kmjblog.domain.CommFileVO;

@Service
public class CommFileUtil {

	private final Path FILE_PATH_BASE;

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".png", ".jpg", ".jpeg", ".gif", ".webp");

	public CommFileUtil(@Value("${file.upload.path}") String fileRootPath) {
    	this.FILE_PATH_BASE = Path.of(fileRootPath).toAbsolutePath().normalize();
	}

	public CommFileVO saveFile(Long postId, MultipartFile file) throws IOException {
		// 0) 파일 유무 확인 
		if(file == null || file.isEmpty()) {
			return new CommFileVO();
		}
		
		// 1) 파일 저장 경로 준비
		String originalFilename = file.getOriginalFilename();
		String extension = extractExtension(originalFilename);
		validateExtension(extension);
		validateImageSignature(file, extension);
		String savedFileName = UUID.randomUUID() + extension;
		Path targetPath = FILE_PATH_BASE.resolve(String.valueOf(postId)).resolve(savedFileName).normalize();

		// 2) 파일 디렉토리 준비 (postId별 하위 디렉토리까지 생성)
		Files.createDirectories(targetPath.getParent());


		// 3) 파일 인풋 스트림열기, 파일 임시 경로에 저장
		try(InputStream inputStream = file.getInputStream()) {
			// 파일 임시 경로에 저장
			Files.copy(inputStream, targetPath);
		}
		
		// 4) CommFileVO 구성
		CommFileVO commFileVo = new CommFileVO();
		commFileVo.setOriginalFileName(originalFilename);
		commFileVo.setSavedFileName(savedFileName);
		// commFileVo.setFilePath(targetPath.toString());
		commFileVo.setFilePath(FILE_PATH_BASE.relativize(targetPath).toString());
		commFileVo.setFileExt(extension);
		commFileVo.setFileSize(file.getSize());
		
		return commFileVo;
	}
	
	public void moveFile(Path fromPath, Path toPath) throws IOException {
		if(fromPath == null || toPath == null) {
			return;
		}
		
		Files.move(fromPath, toPath);
	}
	
	/**
	 * 디스크에 저장된 파일 하나를 삭제한다. (파일이 없으면 무시)
	 * @param filePath
	 */
	public void deleteFile(String filePath) throws IOException {
		if (filePath == null || filePath.isBlank()) {
			return;
		}

		Path targetPath = FILE_PATH_BASE.resolve(filePath).normalize();
		if(!targetPath.startsWith(FILE_PATH_BASE)) {
			throw new IllegalArgumentException("잘못된 파일 경로입니다.");
		}
		Files.deleteIfExists(targetPath);
	}

	/**
	 * 게시글의 업로드 디렉토리(postId 하위 폴더) 전체를 삭제한다. 게시글을 통째로
	 * 지울 때, 파일을 한 건씩 조회/삭제하는 대신 디렉토리 단위로 한 번에 지운다.
	 * @param postId
	 */
	public void deletePostDirectory(Long postId) throws IOException {
		Path dir = FILE_PATH_BASE.resolve(String.valueOf(postId)).normalize();
		if (!Files.exists(dir)) {
			return;
		}

		try (Stream<Path> paths = Files.walk(dir)) {
			// 디렉토리는 비어있어야 지울 수 있으므로, 하위 파일부터 지우도록 역순 정렬
			for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
				Files.deleteIfExists(path);
			}
		}
	}
	
	/**
	 * 파일 확장자 추출 함수
	 * @param filename
	 * @return
	 */
	private String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("원본 파일명이 없습니다.");
        }

        int dotIndex = filename.lastIndexOf('.');

        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }

        return filename.substring(dotIndex).toLowerCase();
    }

	/**
	 * 허용된 확장자인지 검사한다.
	 * @param extension extractExtension()으로 추출한 확장자 (예: ".png")
	 */
	private void validateExtension(String extension) {
		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new IllegalArgumentException("허용되지 않는 파일 형식입니다: " + extension);
		}
	}

	/**
	 * 파일 앞부분(매직 넘버)을 읽어 실제 내용이 확장자와 같은 이미지 형식인지 검사한다.
	 * @param file
	 * @param extension
	 */
	private void validateImageSignature(MultipartFile file, String extension) throws IOException {
		byte[] head;
		try (InputStream in = file.getInputStream()) {
			head = in.readNBytes(12);
		}

		boolean valid;
		switch (extension) {
			case ".png":
				valid = startsWith(head, new byte[]{(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A});
				break;
			case ".jpg":
			case ".jpeg":
				valid = startsWith(head, new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});
				break;
			case ".gif":
				valid = startsWith(head, "GIF87a".getBytes(StandardCharsets.US_ASCII))
					|| startsWith(head, "GIF89a".getBytes(StandardCharsets.US_ASCII));
				break;
			case ".webp":
				valid = head.length >= 12
					&& startsWith(head, "RIFF".getBytes(StandardCharsets.US_ASCII))
					&& Arrays.equals(Arrays.copyOfRange(head, 8, 12), "WEBP".getBytes(StandardCharsets.US_ASCII));
				break;
			default:
				valid = false;
		}

		if (!valid) {
			throw new IllegalArgumentException("파일 내용이 확장자(" + extension + ")와 일치하지 않습니다.");
		}
	}

	private boolean startsWith(byte[] data, byte[] prefix) {
		return data.length >= prefix.length
			&& Arrays.equals(Arrays.copyOf(data, prefix.length), prefix);
	}
}

