package ru.practicum.ewmservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewmservice.model.Comment;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmservice.model.CommentSort;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findCommentByIdAndEvent_Id(Long commentId, Long eventId);

    Optional<Comment> findCommentByIdAndEvent_IdAndAuthor_Id(
            Long commentId, Long eventId, Long authorId);

    @Query("select c from Comment as c " +
            "where c.event.id = ?1 " +
            "order by ?2")
    List<Comment> findCommentsByEvent_Id(Long eventId, CommentSort sort, Pageable pageable);

    @Query("select c from Comment as c " +
            "where c.author.id = ?1 " +
            "order by ?2")
    List<Comment> findCommentsByAuthor_Id(Long authorId, CommentSort sort, Pageable pageable);

    @Query("select c from Comment as c " +
            "order by c.updated")
    List<Comment> findAllCommentsSortedByUpdated(CommentSort sort, Pageable pageable);
}
