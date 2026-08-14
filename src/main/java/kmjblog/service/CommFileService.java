package kmjblog.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kmjblog.domain.CommFileGroupVO;
import kmjblog.domain.CommFileVO;
import kmjblog.mapper.CommFileMapper;
import kmjblog.util.CommFileUtil;

/**
 * 파일 업로드/정리 등, 디스크(CommFileUtil)와 DB(CommFileMapper, CommFileGroupService)를
 * 함께 다뤄야 하는 파일 관련 비즈니스 오퍼레이션을 제공한다.
 */
@Service
public class CommFileService {

	public static final String IMG_URL_BASE = "/blog/file/";

	private final CommFileMapper commFileMapper;
	private final CommFileGroupService commFileGroupService;
	private final CommFileUtil commFileUtil;

	public CommFileService(CommFileMapper commFileMapper, CommFileGroupService commFileGroupService,
			CommFileUtil commFileUtil) {
		this.commFileMapper = commFileMapper;
		this.commFileGroupService = commFileGroupService;
		this.commFileUtil = commFileUtil;
	}

	/**
	 * 파일을 디스크에 저장하고, 게시글의 file_group에 연결해 DB에도 기록한다.
	 * @param postId
	 * @param file
	 * @return
	 */
	public CommFileVO uploadFile(Long postId, MultipartFile file) throws IOException {
		CommFileVO commFileVo = commFileUtil.saveFile(postId, file);

		CommFileGroupVO fileGroup = commFileGroupService.getOrCreateFileGroup(postId);
		commFileVo.setFileGroupId(fileGroup.getFileGroupId());
		commFileMapper.insertFile(commFileVo);

		return commFileVo;
	}

	/**
	 * 게시글 content에 더 이상 등장하지 않는(=참조가 끊긴) 첨부 이미지를 DB와 디스크에서 정리한다.
	 * @param postId
	 * @param content
	 */
	public void deleteUnreferencedFiles(Long postId, String content) throws IOException {
		CommFileGroupVO fileGroup = commFileGroupService.selectFileGroupByPostId(postId);
		if (fileGroup == null) {
			return;
		}

		Set<String> referencedFileNames = extractReferencedFileNames(postId, content);
		List<CommFileVO> files = commFileMapper.selectFilesByFileGroupId(fileGroup.getFileGroupId());

		List<Long> unreferencedFileIds = new ArrayList<>();
		for (CommFileVO file : files) {
			if (referencedFileNames.contains(file.getSavedFileName())) {
				continue;
			}
			commFileUtil.deleteFile(file.getFilePath());
			unreferencedFileIds.add(file.getFileId());
		}

		if (!unreferencedFileIds.isEmpty()) {
			commFileMapper.deleteFilesByIds(unreferencedFileIds);
		}
	}

	/**
	 * 게시글이 삭제될 때, 그 게시글에 딸린 파일을 디스크/DB에서 전부 정리한다.
	 * @param postId
	 */
	public void deleteAllFilesForPost(Long postId) throws IOException {
		CommFileGroupVO fileGroup = commFileGroupService.selectFileGroupByPostId(postId);
		if (fileGroup == null) {
			return;
		}

		commFileUtil.deletePostDirectory(postId);
		commFileMapper.deleteFileByFileGrpId(fileGroup.getFileGroupId());
		commFileGroupService.deleteFileGroupByPostId(postId);
	}

	/**
	 * content 안에서 이 게시글의 업로드 URL 패턴(/blog/file/{postId}/{savedFileName})을 찾아
	 * 실제로 참조되고 있는 saved_file_name 목록을 추출한다.
	 * @param postId
	 * @param content
	 * @return
	 */
	private Set<String> extractReferencedFileNames(Long postId, String content) {
		Set<String> referencedFileNames = new HashSet<>();
		if (content == null || content.isBlank()) {
			return referencedFileNames;
		}

		Pattern pattern = Pattern.compile(Pattern.quote(IMG_URL_BASE + postId + "/") + "([^\\s\"')]+)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			referencedFileNames.add(matcher.group(1));
		}

		return referencedFileNames;
	}
}
