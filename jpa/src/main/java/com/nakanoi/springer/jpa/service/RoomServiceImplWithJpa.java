package com.nakanoi.springer.jpa.service;

import com.nakanoi.springer.jpa.equipment.Equipment;
import com.nakanoi.springer.jpa.room.Room;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;

/** Simple implementation for room service. */
public class RoomServiceImplWithJpa implements RoomService {
  @PersistenceContext private EntityManager entityManager;

  @Override
  @Transactional(readOnly = true)
  public Room getRoom(Integer id) {
    return entityManager.find(Room.class, id);
  }

  @Override
  @Transactional(readOnly = true)
  public Room createRoom(String roomName, Integer capacity) {
    Room room = new Room();
    room.setRoomName(roomName);
    room.setCapacity(capacity);
    entityManager.persist(room);
    return room;
  }

  @Override
  @Transactional(readOnly = true)
  public Room updateRoom(Integer id, String roomName) {
    Room room = getRoom(id);
    room.setRoomName(roomName);
    return room;
  }

  @Override
  @Transactional(readOnly = true)
  public void deleteRoom(Integer id) {
    Room room = getRoom(id);
    entityManager.remove(room);
  }

  @Transactional(readOnly = true)
  public List<Room> getRoomsByNameFetch(String roomName) {
    String jpql =
        "SELECT DISTINCT r FROM Room r LEFT JOIN FETCH r.equipments WHERE r.roomName = :roomName";
    TypedQuery<Room> query = entityManager.createQuery(jpql, Room.class);
    query.setParameter("roomName", roomName);
    return query.getResultList();
  }

  @Transactional
  public Integer updateCapacityAll(Integer capacity) {
    String jpql = "UPDATE Room r SET r.capacity = :capacity";
    Query query = entityManager.createQuery(jpql);
    query.setParameter("capacity", capacity);
    return query.executeUpdate();
  }

  @Transactional(readOnly = true)
  public List<Equipment> getEquipmentsInRoom(Integer roomId) {
    Room room = entityManager.find(Room.class, roomId);
    return room.getEquipments();
  }

  @Transactional(readOnly = true)
  public Room getRoomOfEquipment(Integer equipmentId) {
    Equipment equipment = entityManager.find(Equipment.class, equipmentId);
    return equipment.getRoom();
  }

  @Transactional(readOnly = true)
  public List<Room> getRoomsByName(String roomName) {
    String jpql = "SELECT r FROM Room r WHERE r.roomName = :roomName";
    TypedQuery<Room> query = entityManager.createQuery(jpql, Room.class);
    query.setParameter("roomName", roomName);
    return query.getResultList();
  }

  @Transactional
  public int updateRoomWithOptimisticLock(Integer id, String roomName, Integer capacity) {
    Room room = entityManager.find(Room.class, id);
    entityManager.lock(room, LockModeType.OPTIMISTIC);
    String jpql = "UPDATE Room r SET r.capacity = :capacity WHERE r.roomName = :roomName";
    Query query = entityManager.createQuery(jpql);
    query.setParameter("capacity", capacity);
    query.setParameter("roomName", roomName);
    return query.executeUpdate();
  }
}
