package com.example.memo.service;

import com.example.memo.dto.VideoDto;
import com.example.memo.entity.MemberEntity;
import com.example.memo.entity.VideoEntity;
import com.example.memo.repository.MemberRepository;
import com.example.memo.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository, MemberRepository memberRepository) {
        this.videoRepository = videoRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public VideoEntity saveVideo(VideoDto videoDto) throws Exception {
        String memberEmail = videoDto.getMemberEmail();
        if (memberEmail == null) {
            throw new IllegalArgumentException("Member email is missing in the request.");
        }

        MemberEntity member = memberRepository.findByMemberEmail(memberEmail);  // 멤버 찾기
        if (member == null) {
            /*throw new UserNotFoundException("User not found: " + memberEmail);  // 멤버가 없을 때 예외 처리*/
            throw new Exception();
        }
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setSummary(videoDto.getSummary());
        videoEntity.setDocument(videoDto.getDocument());
        videoEntity.setVideoUrl(videoDto.getVideoUrl());
        videoEntity.setThumbnailUrl(videoDto.getThumbnailUrl());
        videoEntity.setVideoTitle(videoDto.getVideoTitle());
        videoEntity.setMemberEmail(videoDto.getMemberEmail());
        return videoRepository.save(videoEntity);
    }

    //document 내용 추가
    @Transactional
    public VideoEntity updateDocument(String memberEmail, String videoUrl, String newDocument) throws Exception {
        VideoEntity video = videoRepository.findByMemberEmailAndVideoUrl(memberEmail, videoUrl);
        if (video == null) {
            throw new Exception("Video not found with provided email and URL");
        }
        video.setDocument(newDocument);
        return videoRepository.save(video);
    }

    //가장 많이 검색된 video 3개
    @Transactional(readOnly = true)
    public List<VideoDto> findMostFrequentVideos() {
        PageRequest pageRequest = PageRequest.of(0, 3); // 첫 번째 페이지에 3개의 결과
        List<Object[]> results = videoRepository.findMostFrequentVideos(pageRequest);
        return results.stream()
                .map(result -> new VideoDto((String) result[0], (String) result[1], (String) result[2]))
                .collect(Collectors.toList());
    }

    //필기내용 검색
//    @Transactional(readOnly = true)
//    public List<Long> searchVideosByKeyword(String keyword) {
//        return videoRepository.findVideoIdsByDocumentContaining(keyword);
//    }
    @Transactional(readOnly = true)
    public List<VideoEntity> searchVideosByKeyword(String keyword) {
        return videoRepository.findByDocumentContainingIgnoreCase(keyword);
    }
    //video 삭제
    @Transactional
    public boolean deleteVideo(long videoId) {
        if (videoRepository.existsById(videoId)) {
            videoRepository.deleteById(videoId);
            return true;
        } else {
            return false;
        }
    }
}