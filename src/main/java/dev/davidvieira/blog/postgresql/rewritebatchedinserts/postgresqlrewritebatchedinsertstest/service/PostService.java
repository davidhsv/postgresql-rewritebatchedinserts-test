package dev.davidvieira.blog.postgresql.rewritebatchedinserts.postgresqlrewritebatchedinsertstest.service;

import dev.davidvieira.blog.postgresql.rewritebatchedinserts.postgresqlrewritebatchedinsertstest.model.Post;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Service
public class PostService {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void insertPosts(int count) {
        IntStream.range(0, count).forEach(i -> {
            entityManager.persist(new Post(String.format("Post no. %d", i + 1)));
        });
    }
}