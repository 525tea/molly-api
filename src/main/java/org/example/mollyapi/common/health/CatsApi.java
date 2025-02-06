package org.example.mollyapi.common.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.mollyapi.common.dto.CommonResDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "헬스 체크 API", description = "나의 아주 작은 고양이 건강 챙김 API")
@RestController
public class CatsApi {

    @GetMapping("/cat")
    @Operation(summary = "Health Check", description = "HI this API is health Check")
    public ResponseEntity<CommonResDto> getCat() {
        return ResponseEntity.ok(new CommonResDto("cat is very very very cute :), you know?"));
    }
}
