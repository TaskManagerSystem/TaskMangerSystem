package kafkademo.taskmanagersystem.repo;

import java.time.LocalDate;
import java.util.List;
import kafkademo.taskmanagersystem.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p JOIN p.users u WHERE u.id = :userId")
    List<Project> findAllByUserId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"users"})
    @Query("SELECT p FROM Project p "
            + "WHERE p.endDate = :today "
            + "AND p.status != COMPLETED")
    List<Project> findProjectsWithDueDateTodayAndNotCompleted(@Param("today") LocalDate today);
}
