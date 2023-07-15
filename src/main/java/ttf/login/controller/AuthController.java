package ttf.login.controller;

import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * packageName :  ttf.login.controller
 * fileName : AuthController
 * author :  ddh96
 * date : 2023-07-16 
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-07-16                ddh96             최초 생성
 */
@RestController
@Slf4j
public class AuthController {
    private static final String staticURL="https://lostark.game.onstove.com/Community/Free/Views/";

    //로그인한 유저에 대해서 인증이 가능하도록 하게 함
    @GetMapping("/getCredits")
    public String getValidateCredentials(HttpSession session) {
        String randomString = UUID.randomUUID().toString().split("-")[0];
        String userId = (String)session.getAttribute("userId");
        session.setMaxInactiveInterval(1800);
        session.setAttribute(userId, randomString);
        return randomString;
    }
    @GetMapping("/mockLogin")
    public void mockLogin(HttpSession session,@RequestParam("id") String id,@RequestParam("username") String username) {
        //사이트 id
        session.setAttribute("userId",id);
        //인증하려는 유저네임
        session.setAttribute("username",username);

        log.info("id : {}",id);
        log.info("username : {}",username);
    }

    @GetMapping("/certification/{number}")
    public void certification(@PathVariable Long number,HttpSession session) {

        String userId = (String)session.getAttribute("userId");
        //이건 원래 따로 관리해야함
        String certificationNickname =(String)session.getAttribute("username");
        String certificationNumber = (String)session.getAttribute(userId);

        String url = UriComponentsBuilder.fromHttpUrl(staticURL)
            .path(String.valueOf(number)).toUriString();

        try {

            Document doc = Jsoup.connect(url).get();
            Element authorDiv = doc.selectFirst("div.article__author");
            Element titleElement = doc.selectFirst("span.article__title");
            Element nameSpan = authorDiv.selectFirst("span.character-info__name");

            String parseAuthenticationNumber = titleElement.text();
            String parseUsername = nameSpan.attr("title");

            if (certificationNumber == null || certificationNickname == null) {
                throw new Exception("데이터가 null입니다.");
            }

            log.info("파싱 번호 : {}",parseAuthenticationNumber);
            log.info("인증 번호 : {}",certificationNumber);
            log.info("파싱 닉네임 : {}",parseUsername);
            log.info("인증 닉네임 : {}",certificationNickname);

            log.info("인증번호 성공여부 : {}", certificationNumber.equals(parseAuthenticationNumber));
            log.info("인증 닉네임 성공여부  : {}", certificationNickname.equals(parseUsername));
            if (certificationNumber.equals(parseAuthenticationNumber) && certificationNickname.equals(parseUsername)) {
                //session.removeAttribute("username");
                session.removeAttribute(userId);
                log.info("인증 성공");
            } else {
                log.info("인증 실패");
            }
        } catch (Exception e) {
            log.error("인증 실패");
            e.printStackTrace();
        }

    }


}
