package com.blog.article.controller.dto.response;

import com.blog.article.entity.Article;
import lombok.Getter;

@Getter
public class ArticleResponse {

    private final String author;
    private final String title;
    private final String content;

    public ArticleResponse(Article article) {
        this.author = article.getAuthor();
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
