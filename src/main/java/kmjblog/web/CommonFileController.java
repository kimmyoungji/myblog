package kmjblog.web;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kmjblog.domain.ApiResponse;
import kmjblog.domain.CommFileVO;
import kmjblog.service.CommFileService;

@Controller
@RequestMapping("/blog/api/file")
public class CommonFileController {

	private final CommFileService commFileService;

	public CommonFileController(CommFileService commFileService) {
		this.commFileService = commFileService;
	}
	
	/**
	 * vditor 첨부 이미지 멀티파트 파일 단건 업로드
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/upload/vditor/{postId}")
	@ResponseBody
	public ResponseEntity<ApiResponse<Map<String, Object>>> fileUpload(
			@PathVariable Long postId,
			@RequestParam("file") MultipartFile file
	) throws IOException {
		CommFileVO commFileVo = commFileService.uploadFile(postId, file);

		Path base = Path.of(CommFileService.IMG_URL_BASE);
		Path url = base.resolve(String.valueOf(postId)).resolve(commFileVo.getSavedFileName());
		String originalFilename = commFileVo.getOriginalFileName();
		
		// 5) 파일 정보와 url 을 포함하여 vditor에 맞는 응답 정보 구성		
		Map<String, Object> vditorResData = this.createVditorUploadResData(originalFilename, url.toString());
		
		// 응답 반환
		ApiResponse<Map<String, Object>> apiResponse = new ApiResponse<Map<String, Object>>(true, "파일 업로드 성공", vditorResData);
		return ResponseEntity.ok(apiResponse);
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
