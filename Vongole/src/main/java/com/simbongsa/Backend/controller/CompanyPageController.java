package com.simbongsa.Backend.controller;

import com.simbongsa.Backend.dto.request.CompanyUpdateRequest;
import com.simbongsa.Backend.dto.response.*;
import com.simbongsa.Backend.entity.UserDetailsImpl;
import com.simbongsa.Backend.service.CompanyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companypage")
// TODO secured 어노테이션 공부
//@Secured()
public class CompanyPageController {
    private final CompanyPageService companyPageService;

    /**
     * 내 프로필 정보 조회
     */
    @GetMapping()
    public ResponseDto<CompanyResponse> getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return companyPageService.getMyProfile(userDetails.getMember());
    }


    /**
     * 내 프로필 정보 수정
     */
    @PutMapping()
    public ResponseDto<CompanyResponse> updateMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                        @ModelAttribute CompanyUpdateRequest companyUpdateRequest) throws IOException {
        return companyPageService.updateMyProfile(userDetails.getMember(), companyUpdateRequest);
    }


    /**
     * 내가 작성한 게시물 조회
     */
    @GetMapping("/boards")
    public ResponseDto<List<BoardResponse>> getMyBoards(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return companyPageService.getMyBoards(userDetails.getMember());
    }

    /**
     * 봉사 활동 지원자 목록
     */
    @GetMapping("/boards/{boardId}")
    public ResponseDto<List<EnrollResponse>> getVolunteers(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           @PathVariable Long boardId) {
        return companyPageService.getVolunteers(userDetails.getMember(), boardId);
    }


    /**
     * 지원자 승인
     */
    @PutMapping("/approve/{enrollId}")
    public ResponseDto<MsgResponse> approveMember(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @PathVariable Long enrollId) {
        return companyPageService.approveMember(userDetails.getMember(), enrollId);
    }

    /**
     * 지원자 거절
     */
    @PutMapping("/disapprove/{enrollId}")
    public ResponseDto<MsgResponse> disapproveMember(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                     @PathVariable Long enrollId) {
        return companyPageService.disapproveMember(userDetails.getMember(), enrollId);
    }
}
