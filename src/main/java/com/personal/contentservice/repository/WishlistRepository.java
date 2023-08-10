package com.personal.contentservice.repository;

import com.personal.contentservice.domain.Content;
import com.personal.contentservice.domain.User;
import com.personal.contentservice.domain.Wishlist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long>  {

  List<Wishlist> findByUser(User user);

  Optional<Wishlist> findByUserAndContent(User user, Content content);

}
