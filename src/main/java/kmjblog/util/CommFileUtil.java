package kmjblog.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kmjblog.domain.CommFileVO;

@Service
public class CommFileUtil {
	
	private static final Path FILE_PATH_BASE = Path.of("/Users/myoungjikim/dev/kbc_std/myblog/blog/upload").toAbsolutePath().normalize();
	private static final String TEMP_IMG_URL_BASE = "/blog/file/temp/";
	
	public CommFileVO saveFile(Long postId, MultipartFile file) throws IOException {
		// 0) 파일 유무 확인 
		if(file == null || file.isEmpty()) {
			return new CommFileVO();
		}
		
		// 1) 파일 저장 경로 준비
		String originalFilename = file.getOriginalFilename();
		String extension = extractExtension(originalFilename);
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
		commFileVo.setFilePath(targetPath.toString());
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

		Files.deleteIfExists(Path.of(filePath));
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
}
