package com.blog.article.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blog.article.controller.dto.request.AddArticleRequest;
import com.blog.article.controller.dto.request.UpdateArticleRequest;
import com.blog.article.entity.Article;
import com.blog.article.respository.BlogRepository;
import com.blog.article.service.BlogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {

    @Autowired
    //mock of mvc pattern
    private MockMvc api;

    @Autowired
    //java object -> json
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private BlogRepository blogRepository;
    private BlogService blogService;

    @BeforeEach
    public void setApi() {
        this.api = MockMvcBuilders.webAppContextSetup(context)
                .build();
        blogRepository.deleteAll();
    }

    @DisplayName("[Success] 새로운 글을 추가하는 작업에 성공합니다.")
    @Test
    public void addArticle() throws Exception {
        final String uri = "/api/articles";
        final String title = "제목";
        final String content = "글";
        final AddArticleRequest addArticleRequest = new AddArticleRequest(title, content);

        //json 으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(addArticleRequest);

        ResultActions result = api.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles).hasSize(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("[Success] 모든 글을 조회하는 작업에 성공합니다.")
    @Test
    public void findArticles() throws Exception {
        final String uri = "/api/articles";
        final Article article = Article.builder()
                .title("글")
                .content("제목")
                .build();

        blogRepository.save(article);

        final ResultActions resultAction = api.perform(get(uri)
                .accept(MediaType.APPLICATION_JSON));

        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(article.getTitle()))
                .andExpect(jsonPath("$[0].content").value(article.getContent()));
    }

    @DisplayName("[Success] 아이디에 해당하는 글을 조회하는 작업에 성공합니다.")
    @Test
    public void findArticleById() throws Exception {
        final String uri = "/api/articles/{id}";
        final String title = "ex title";
        final String content = "ex content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        final ResultActions resultActions = api.perform(get(uri, savedArticle.getId()));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content));
    }

    @DisplayName("[Success] 아이디에 해당하는 글을 삭제하는 작업에 성공합니다.")
    @Test
    public void deleteArticleById() throws Exception {
        final String uri = "/api/articles/{id}";
        final String title = "ex title";
        final String content = "ex content";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        final ResultActions resultActions = api.perform(delete(uri, savedArticle.getId()));
        resultActions.andExpect(status().isOk());

        List<Article> articles = blogRepository.findAll();
        Assertions.assertTrue(articles.isEmpty());
    }

    @DisplayName("[Success] 글의 제목과 글을 수정하는 작업에 성공합니다.")
    @Test
    public void updateArticle() throws Exception {
        final String uri = "/api/articles/{id}";
        final String title = "기존의 제목";
        final String content = "기존의 글";

        Article article = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        final String newTitle = "new title";
        final String newContent = "new content";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        final ResultActions resultActions = api.perform(post(uri, article.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        Article updatedArticle = blogRepository.findById(article.getId()).get();

        resultActions.andExpect(status().isOk());

        assertThat(updatedArticle.getTitle()).isEqualTo(newTitle);
        assertThat(updatedArticle.getContent()).isEqualTo(newContent);
    }
}