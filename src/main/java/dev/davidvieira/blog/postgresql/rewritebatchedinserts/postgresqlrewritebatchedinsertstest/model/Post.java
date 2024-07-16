package dev.davidvieira.blog.postgresql.rewritebatchedinserts.postgresqlrewritebatchedinsertstest.model;


import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity(name = "Post")
@Table(name = "post")
public class Post {
 
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "post_seq"
    )
    // replace when this is finished https://github.com/vladmihalcea/hypersistence-utils/issues/728
    @GenericGenerator(
            name = "post_seq",
            strategy = "io.hypersistence.utils.hibernate.id.BatchSequenceGenerator",
            parameters = {
                    @Parameter(name = "sequence", value = "post_seq"),
                    @Parameter(name = "fetch_size", value = "4096")
            }
    )
    @Column(nullable = false)
    private Long id;
 
    private String title;
 
    public Post() {}
 
    public Post(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Post post = (Post) o;
        return getId() != null && Objects.equals(getId(), post.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "title = " + title + ")";
    }
}