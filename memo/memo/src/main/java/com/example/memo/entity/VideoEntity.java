package com.example.memo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "video_table")
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long videoId;

    /*gpt 동영상요약내용*/
    @Column
    private String summary;

    /*필기내용*/
    @Column
    private String document;

    /*유튜브영상 url*/
    @Column
    private String videoUrl;

    /*썸네일 url*/
    @Column
    private String thumbnailUrl;

    /*유튜브영상 제목*/
    @Column
    private String videoTitle;

    /*사용자가 직접 추가하는 카테고리*/
    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "member_email")
    private String memberEmail;

    @Column(name = "document_date")
    private LocalDate documentDate;
}
