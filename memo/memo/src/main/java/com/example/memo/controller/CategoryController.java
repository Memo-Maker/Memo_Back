package com.example.memo.controller;

import com.example.memo.dto.CategoryDto;
import com.example.memo.dto.VideoDto;
import com.example.memo.dto.VideoFolderRequestDto;
import com.example.memo.entity.VideoEntity;
import com.example.memo.repository.VideoRepository;
import com.example.memo.service.CategoryService;
import com.example.memo.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final VideoService videoService;

    @Autowired
    public CategoryController(CategoryService categoryService, VideoService videoService){
        this.categoryService=categoryService;
        this.videoService = videoService;
    }
    //category 추가
    @PostMapping("/create")
    @CrossOrigin("*")
    public ResponseEntity<Void> addCategory(@RequestBody CategoryDto categoryDto) throws Exception {
        categoryService.addCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 폴더에 비디오 추가
    @PostMapping("/add-video")
    @CrossOrigin("*")
    public ResponseEntity<Void> addVideoToFolder(@RequestBody VideoFolderRequestDto request) {
        try {
            videoService.addVideoToFolder(request.getMemberEmail(), request.getVideoUrl(), request.getCategoryName());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 비디오를 찾지 못한 경우 404 응답
        }
    }

    // 카테고리 이름 수정 엔드포인트
    @PutMapping("/update")
    @CrossOrigin("*")
    public ResponseEntity<Void> updateCategoryName(@RequestParam("memberEmail") String memberEmail,
                                                   @RequestParam("oldCategoryName") String oldCategoryName,
                                                   @RequestParam("newCategoryName") String newCategoryName) {
        try {
            categoryService.updateCategoryName(memberEmail, oldCategoryName, newCategoryName);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 해당 카테고리가 없는 경우 404 응답
        }
    }

    //category 삭제
    @DeleteMapping("delete/{categoryName}")
    @CrossOrigin("*")
    public ResponseEntity<?> deleteCategory(@PathVariable("categoryName") String categoryName) {
        if (categoryService.deleteCategory(categoryName)) {
            return ResponseEntity.ok().build(); // 성공적으로 삭제되면 200 OK 응답
        } else {
            return ResponseEntity.ok(false);    //실패하면 false 반환
        }
    }
}
