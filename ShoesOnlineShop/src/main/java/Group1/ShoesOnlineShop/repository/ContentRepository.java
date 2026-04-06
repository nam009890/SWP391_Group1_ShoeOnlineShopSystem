/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Group1.ShoesOnlineShop.repository;

import Group1.ShoesOnlineShop.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContentRepository extends JpaRepository<Content, Long> ,JpaSpecificationExecutor<Content>{
   Page<Content> findByContentTitleContainingIgnoreCase(String keyword, Pageable pageable);
   
   java.util.List<Content> findTop5ByOrderByCreatedAtDesc();
   java.util.List<Content> findTop50ByOrderByCreatedAtDesc();

    // Active content for home page
    java.util.List<Content> findByIsActiveTrueOrderByCreatedAtDesc();

    // Active + Approved content for homepage (only show approved content)
    java.util.List<Content> findByIsActiveTrueAndApprovalStatusOrderByCreatedAtDesc(String approvalStatus);

    // Duplicate title check
    boolean existsByContentTitle(String contentTitle);
    boolean existsByContentTitleAndIdNot(String contentTitle, Long id);
    
    Page<Content> findByApprovalStatus(String approvalStatus, Pageable pageable);
<<<<<<< HEAD
    Page<Content> findByApprovalStatusAndContentTitleContainingIgnoreCase(String approvalStatus, String keyword, Pageable pageable);
=======
>>>>>>> 088cea8310666489ea9c06a81f5a59706a724daa
}