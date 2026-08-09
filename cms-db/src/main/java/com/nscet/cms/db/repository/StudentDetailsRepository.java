package com.nscet.cms.db.repository;

import com.nscet.cms.db.entity.StudentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentDetailsRepository extends JpaRepository<StudentDetails, Long> {

    List<StudentDetails> findByStudentIdAndAcademicYear(Long studentId, String academicYear);

    @Query("SELECT sd FROM StudentDetails sd WHERE sd.isActive = true " +
           "AND sd.department.id = :deptId AND sd.semester = :semester " +
           "AND sd.academicYear = :academicYear")
    List<StudentDetails> findByDeptAndSemester(@Param("deptId") Long deptId,
                                                @Param("semester") Integer semester,
                                                @Param("academicYear") String academicYear);
}
