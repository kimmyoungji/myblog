package kmjblog.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kmjblog.domain.ApiResponse;

@Controller
@RequestMapping("/blog/api/file")
public class CommonFileController {
	
	private static final Path TEMP_FILE_PATH = Path.of("/Users/myoungjikim/dev/kbc_std/myblog/blog/upload/temp").toAbsolutePath().normalize();
	private static final Path FILE_PATH_BASE = Path.of("/Users/myoungjikim/dev/kbc_std/myblog/blog/upload").toAbsolutePath().normalize();
	private static final String TEMP_IMG_URL_BASE = "/blog/file/temp/";
	
	/**
	 * 멀티파트 파일 단건 임시 업로드
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/upload")
	@ResponseBody
	public ResponseEntity<ApiResponse<Map<String, Object>>> fileUpload(
			@RequestParam("file") MultipartFile file
	) throws IOException {
		Map<String, Object> result = new LinkedHashMap<>();
		
		// 0) 파일 유무 확인 
		if(file == null || file.isEmpty()) {
			ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<Map<String, Object>>(false, "파일 임시 업로드 실패", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
		}
		
		// 1) 파일 디렉토리 준비
		Files.createDirectories(TEMP_FILE_PATH);
		
		// 2) 파일 저장 경로 준비
		String originalFilename = file.getOriginalFilename();
		String extension = extractExtension(originalFilename);
		String storedFilename = UUID.randomUUID() + extension;
		Path targetPath = TEMP_FILE_PATH.resolve(storedFilename).normalize();
		
		
		// 3) 파일 인풋 스트림열기, 파일 임시 경로에 저장
		try(InputStream inputStream = file.getInputStream()) {
			// 파일 임시 경로에 저장
			Files.copy(inputStream, targetPath);
		}
		
		// 4) url 준비
		String url = TEMP_IMG_URL_BASE + storedFilename;
		
		// 5) 파일 정보와 url 을 포함하여 vditor에 맞는 응답 정보 구성		
		Map<String, Object> vditorResData = this.createVditorUploadResData(originalFilename, url);
		
		// 응답 반환
		ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<Map<String, Object>>(true, "파일 임시 업로드 성공", vditorResData);
		return ResponseEntity.ok(apiResponse);
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
	 * vditor 이미지 업로드 응답 데이터 생성함수
	 * @param originalFilename
	 * @param imageUrl
	 * @return
	 */
	private Map<String, Object> createVditorUploadResData(String originalFilename,String imageUrl) {
	    Map<String, String> succMap =
	            new LinkedHashMap<>();

	    succMap.put(originalFilename, imageUrl);

	    Map<String, Object> data =
	            new LinkedHashMap<>();

	    data.put("errFiles", Collections.emptyList());
	    data.put("succMap", succMap);

	    return data;
	}
}
