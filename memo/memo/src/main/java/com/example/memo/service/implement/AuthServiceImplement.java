package com.example.memo.service.implement;

import com.example.memo.dto.*;
import com.example.memo.entity.CategoryEntity;
import com.example.memo.entity.MemberEntity;
import com.example.memo.provider.JwtProvider;
import com.example.memo.repository.CategoryRepository;
import com.example.memo.repository.MemberRepository;
import com.example.memo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final JwtProvider jwtProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public ResponseEntity<? super SignUpResponseDto> signUp(MemberDTO dto) {

        try {
            String memberEmail = dto.getMemberEmail();
            boolean existedMemberEmail = memberRepository.existsByMemberEmail(memberEmail);
            if(existedMemberEmail) return SignUpResponseDto.duplicateEmail();

            String memberName = dto.getMemberName();
            boolean existedMemberName = memberRepository.existsByMemberName(memberName);
            if (existedMemberName) return SignUpResponseDto.duplicateName();

            String memberPassword = dto.getMemberPassword();
            String encodedPassword = passwordEncoder.encode(memberPassword); //password 암호화
            dto.setMemberPassword(encodedPassword); //암호화된 password 저장

            MemberEntity memberEntity = new MemberEntity(dto);
            memberRepository.save(memberEntity);

            // 기본 카테고리 생성
            CategoryEntity defaultCategory = new CategoryEntity();
            defaultCategory.setCategoryName("최근 본 영상"); // 원하는 이름으로 설정
            //defaultCategory.setCategorySequence(1); // 순서가 필요한 경우
            defaultCategory.setMemberEmail(memberEmail); // 회원의 이메일로 소유자 설정
            categoryRepository.save(defaultCategory); // 데이터베이스에 저장

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return SignUpResponseDto.success();
    }

    @Override
    public ResponseEntity<? super SignInResponseDto> signIn(SignInRequestDto dto) {

        String token = null;

        try{
            String memberEmail = dto.getMemberEmail();
            MemberEntity memberEntity = memberRepository.findByMemberEmail(memberEmail);
            if (memberEntity == null) return SignInResponseDto.signInFailed();

            String memberPassword = dto.getMemberPassword();
            String encodedPassword = memberEntity.getMemberPassword();
            boolean isMatched = passwordEncoder.matches(memberPassword, encodedPassword);
            if(!isMatched) return SignInResponseDto.signInFailed();

            token = jwtProvider.create(memberEmail);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return SignInResponseDto.success(token);
    }
}
