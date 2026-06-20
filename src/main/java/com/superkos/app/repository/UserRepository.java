package com.superkos.app.repository;

import com.superkos.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
// #naufal(User)
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user WHERE email = ?1", nativeQuery = true)
    void deleteOrphanedByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE u FROM user u LEFT JOIN pencari_hunian ph ON u.id = ph.id LEFT JOIN pemilik_properti pp ON u.id = pp.id LEFT JOIN admin a ON u.id = a.id WHERE ph.id IS NULL AND pp.id IS NULL AND a.id IS NULL", nativeQuery = true)
    void deleteOrphanedUsers();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM roommate_request WHERE pencari_hunian_id = ?1 OR target_pencari_id = ?1", nativeQuery = true)
    void deleteRoommateRequestsByUserId(int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM reservasi WHERE pencari_hunian_id = ?1 OR pemilik_id = ?1", nativeQuery = true)
    void deleteReservasiByUserId(int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM chat_room_participants WHERE user_id = ?1", nativeQuery = true)
    void deleteChatRoomParticipantsByUserId(int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM message WHERE sender_id = ?1", nativeQuery = true)
    void deleteMessagesByUserId(int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM wishlist WHERE pencari_hunian_id = ?1", nativeQuery = true)
    void deleteWishlistsByUserId(int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM hunian_kategori WHERE hunian_id IN (SELECT id_hunian FROM hunian WHERE pemilik_id = ?1)", nativeQuery = true)
    void deleteHunianKategoriByPemilikId(int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM hunian_foto WHERE hunian_id IN (SELECT id_hunian FROM hunian WHERE pemilik_id = ?1)", nativeQuery = true)
    void deleteHunianFotoByPemilikId(int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM hunian_fasilitas WHERE hunian_id IN (SELECT id_hunian FROM hunian WHERE pemilik_id = ?1)", nativeQuery = true)
    void deleteHunianFasilitasByPemilikId(int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM wishlist WHERE hunian_id IN (SELECT id_hunian FROM hunian WHERE pemilik_id = ?1)", nativeQuery = true)
    void deleteWishlistHunianByPemilikId(int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM message WHERE chat_room_id IN (SELECT id_chat FROM chat_room WHERE hunian_id IN (SELECT id_hunian FROM hunian WHERE pemilik_id = ?1))", nativeQuery = true)
    void deleteChatRoomMessagesByPemilikId(int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM chat_room_participants WHERE chat_room_id IN (SELECT id_chat FROM chat_room WHERE hunian_id IN (SELECT id_hunian FROM hunian WHERE pemilik_id = ?1))", nativeQuery = true)
    void deleteChatRoomParticipantsByPemilikId(int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM reservasi WHERE hunian_id IN (SELECT id_hunian FROM hunian WHERE pemilik_id = ?1)", nativeQuery = true)
    void deleteReservasiByHunianPemilikId(int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM chat_room WHERE hunian_id IN (SELECT id_hunian FROM hunian WHERE pemilik_id = ?1)", nativeQuery = true)
    void deleteChatRoomsByPemilikId(int userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM hunian WHERE pemilik_id = ?1", nativeQuery = true)
    void deleteHunianByPemilikId(int userId);

    @Transactional
    default void deleteUserCascade(int userId) {
        deleteRoommateRequestsByUserId(userId);
        deleteReservasiByUserId(userId);
        deleteChatRoomParticipantsByUserId(userId);
        deleteMessagesByUserId(userId);
        deleteWishlistsByUserId(userId);

        deleteHunianKategoriByPemilikId(userId);
        deleteHunianFotoByPemilikId(userId);
        deleteHunianFasilitasByPemilikId(userId);
        deleteWishlistHunianByPemilikId(userId);
        deleteChatRoomMessagesByPemilikId(userId);
        deleteChatRoomParticipantsByPemilikId(userId);
        deleteReservasiByHunianPemilikId(userId);
        deleteChatRoomsByPemilikId(userId);
        deleteHunianByPemilikId(userId);

        deleteById(userId);
    }
}
// #/naufal(User)
