package ru.practicum.ewmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmservice.model.Comment;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findCommentByIdAndEvent_Id(Long commentId, Long eventId);

    Optional<Comment> findCommentByIdAndEvent_IdAndAuthor_Id(Long commentId, Long eventId, Long authorId);

    List<Comment> findCommentsByEvent_IdOrderByCreatedDesc(Long eventId, Pageable pageable);

    List<Comment> findCommentsByAuthor_IdOrderByCreatedDesc(Long authorId, Pageable pageable);

    List<Comment> findCommentsByVisibleIsOrderByCreatedDesc(boolean visible, Pageable pageable);

    @Query("select c from Comment as c " +
            "order by c.created desc")
    List<Comment> findAllComments(Pageable pageable);
}
