package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.StudentDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentDetailsRepository extends JpaRepository<StudentDetails, Long> {

    List<StudentDetails> findByStudentIdAndAcademicYear(Long studentId, String academicYear);

    @Query("SELECT sd FROM StudentDetails sd WHERE sd.isActive = true " +
           "AND sd.department.id = :deptId AND sd.semester = :semester " +
           "AND sd.academicYear = :academicYear")
    List<StudentDetails> findByDeptAndSemester(@Param("deptId") Long deptId,
                                                @Param("semester") Integer semester,
                                                @Param("academicYear") String academicYear);

    @Query("SELECT sd FROM StudentDetails sd " +
           "LEFT JOIN FETCH sd.student " +
           "LEFT JOIN FETCH sd.department " +
           "LEFT JOIN FETCH sd.quota " +
           "WHERE sd.isActive = true")
    Page<StudentDetails> findAllActive(Pageable pageable);

    @Query("SELECT sd FROM StudentDetails sd " +
           "LEFT JOIN FETCH sd.student " +
           "LEFT JOIN FETCH sd.department " +
           "LEFT JOIN FETCH sd.quota " +
           "WHERE sd.isActive = true " +
           "AND (LOWER(sd.student.rollNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(sd.student.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(sd.student.registrationNo) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<StudentDetails> search(@Param("search") String search, Pageable pageable);

    @Query("SELECT sd FROM StudentDetails sd " +
           "LEFT JOIN FETCH sd.student " +
           "LEFT JOIN FETCH sd.department " +
           "LEFT JOIN FETCH sd.quota " +
           "WHERE sd.isActive = true AND sd.id = :id")
    Optional<StudentDetails> findByIdActive(@Param("id") Long id);

    @Query("SELECT sd FROM StudentDetails sd " +
           "WHERE sd.isActive = true " +
           "AND sd.student.id = :studentId AND sd.semester = :semester " +
           "AND sd.academicYear = :academicYear")
    Optional<StudentDetails> findByStudentAndSemesterAndYear(
            @Param("studentId") Long studentId,
            @Param("semester") Integer semester,
            @Param("academicYear") String academicYear);

    @Query("SELECT COUNT(sd) FROM StudentDetails sd " +
           "WHERE sd.isActive = true AND sd.student.id = :studentId " +
           "AND sd.semester = :semester AND sd.academicYear = :academicYear " +
           "AND sd.id <> :excludeId")
    long countDuplicateExcept(@Param("studentId") Long studentId,
                              @Param("semester") Integer semester,
                              @Param("academicYear") String academicYear,
                              @Param("excludeId") Long excludeId);

    @Query("SELECT COUNT(sd) FROM StudentDetails sd " +
           "WHERE sd.isActive = true AND sd.student.id = :studentId " +
           "AND sd.semester = :semester AND sd.academicYear = :academicYear")
    long countDuplicate(@Param("studentId") Long studentId,
                        @Param("semester") Integer semester,
                        @Param("academicYear") String academicYear);
}
