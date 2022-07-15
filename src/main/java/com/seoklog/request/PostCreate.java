package com.seoklog.request;

import com.seoklog.exception.InvalidRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
@Setter
@Getter
public class PostCreate {

    @NotBlank(message = "타이틀을 입력해주세요")
    private String title;
    @NotBlank(message = "내용을 입력해주세요")
    private String content;


    //빌더의 장점
    // - 가독성에 좋음 (값 생성에 대해 유연함 , 빌더 패턴을 사용하여 생성자 파라미터의 순서가 바뀌어도 외부에 영향가지 않게 함)
    // - 필요한 값만 받을 수 있다.
    // - 객체의 불변성 (필드에 final 키워드가 붙었을경우 , 객체 생성 비즈니스 메소드를 사용하여 새로운 객체를 만들어내어야함)
    @Builder
    public PostCreate(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void validate() {
        if(title.contains("바보"))
            throw new InvalidRequest("title" , "제목에 바보를 포함할 수 없습니다.");
    }
}
