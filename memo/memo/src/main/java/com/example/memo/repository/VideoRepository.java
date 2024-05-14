package com.example.memo.repository;

import com.example.memo.entity.VideoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
    boolean existsByVideoUrlAndMemberEmail(String videoUrl, String memberEmail);
    //가장 많이 저장된 video 3개 찾는 query
    @Query("SELECT v.videoTitle, v.thumbnailUrl, v.videoUrl, COUNT(v.videoUrl) AS count FROM VideoEntity v GROUP BY v.videoTitle, v.thumbnailUrl, v.videoUrl ORDER BY count DESC")
    List<Object[]> findMostFrequentVideos(Pageable pageable);

    //필기내용 검색 query
    List<VideoEntity> findVideoByDocumentContainingAndMemberEmail(String keyword, String memberEmail);

    VideoEntity findByMemberEmailAndVideoUrl(String memberEmail, String videoUrl);
    VideoEntity findByVideoUrl(String videoUrl);
    //category별 video 검색
    List<VideoEntity> findByCategoryNameAndMemberEmail(String categoryName, String memberEmail);
    //category삭제
    List<VideoEntity> findByCategoryName(String categoryName);


    @Modifying
    @Transactional
    @Query("UPDATE VideoEntity v SET v.categoryName = :newCategoryName WHERE v.memberEmail = :memberEmail AND v.categoryName = :oldCategoryName")
    int updateCategoryNameForMember(@Param("memberEmail") String memberEmail, @Param("oldCategoryName") String oldCategoryName, @Param("newCategoryName") String newCategoryName);
}