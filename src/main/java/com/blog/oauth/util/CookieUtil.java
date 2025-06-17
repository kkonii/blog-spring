package com.blog.oauth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;
import org.springframework.util.SerializationUtils;

public class CookieUtil {

    //요청값을 기반으로 쿠키를 추가
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        // '/' 경로 이하의 url 에서만 name cookie 에 접근이 가능하다
        // 경로를 설정하지 않을 경우 해당 도메인의 전체 경로에서 유효함
        cookie.setPath("/");

        //만료 기간
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    //쿠키 이름을 입력받아 쿠키 삭제
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    //객체를 직렬화하여 쿠키에 들어갈 수 있는 값으로 변환
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    //쿠키 값을 역직렬화해 객체로 변환
    public static <T> T deserialize(Cookie cookie, Class<T> clazz) {
        return clazz.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue()))
        );
    }
}
