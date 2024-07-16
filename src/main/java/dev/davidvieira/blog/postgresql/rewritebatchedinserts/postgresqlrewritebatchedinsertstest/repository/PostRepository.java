package dev.davidvieira.blog.postgresql.rewritebatchedinserts.postgresqlrewritebatchedinsertstest.repository;

import dev.davidvieira.blog.postgresql.rewritebatchedinserts.postgresqlrewritebatchedinsertstest.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}