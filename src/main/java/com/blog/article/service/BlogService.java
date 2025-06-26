package com.blog.article.service;

import com.blog.article.controller.dto.request.AddArticleRequest;
import com.blog.article.controller.dto.request.UpdateArticleRequest;
import com.blog.article.entity.Article;
import com.blog.article.respository.BlogRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("can not find" + id));
    }

    public void deleteById(Long id) {
        blogRepository.deleteById(id);
    }

    @Transactional
    public Article update(Long id, UpdateArticleRequest request) {
        Article article = findById(id);
        article.update(request.getTitle(), request.getContent());

        return article;
    }
}
