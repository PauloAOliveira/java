package com.usecases.spring.repository;

import com.usecases.spring.domain.DocumentType;
import com.usecases.spring.domain.Person;
import com.usecases.spring.exception.ConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Optional;

@Repository
public class PersonRepository extends BaseRepository{

    private static final String INSERT = "INSERT INTO Person " +
            "(documentType, documentNumber, firstName, lastName, email, birthDate, created, lastUpdate) " +
            "VALUES (?, ?, ?, ?, ?, ?, now(), now())";

    private static final String SELECT_BY_ID = "SELECT * FROM Person WHERE id = ? AND deleted = 0";
    private static final String SELECT_EXIST = "SELECT COUNT(*) FROM Person WHERE id = ? AND deleted = 0";
    private static final String LOGICAL_DELETE = "UPDATE Person SET deleted = 1 WHERE id = ?";
    private static final String UPDATE_BY_ID = "UPDATE Person SET firstName = ?, lastName = ?, email = ?, birthDate = ?, lastUpdate = now() WHERE id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Person> mapper = (rs, i) -> {
        Person person = new Person();
        person.setId(rs.getLong("id"));
        person.setLastName(rs.getString("lastName"));
        person.setEmail(rs.getString("email"));
        person.setFirstName(rs.getString("firstName"));
        person.setBirthDate(getAsLocalDate(rs, "birthDate"));
        person.setCreated(getAsLocalDateTime(rs, "created"));
        person.setLastUpdate(getAsLocalDateTime(rs, "lastUpdate"));
        person.setDocumentNumber(rs.getString("documentNumber"));
        person.setDocumentType(DocumentType.valueOf(rs.getString("documentType")));
        person.setDeleted(rs.getBoolean("deleted"));
        return person;
    };

    public Boolean exist(Long id) {
        Long count = jdbcTemplate.queryForObject(SELECT_EXIST, Long.class, id);
        return count > 0;
    }

    public Optional<Person> getById(Long id) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(SELECT_BY_ID, mapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Long create(Person person) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator statement = c -> {
            PreparedStatement ps = c.prepareStatement(INSERT);
            ps.setString(1, person.getDocumentType().name());
            ps.setString(2, person.getDocumentNumber());
            ps.setString(3, person.getFirstName());
            ps.setString(4, person.getLastName());
            ps.setString(5, person.getEmail());
            ps.setString(6, person.getBirthDate().toString());
            return ps;
        };

        try {
            jdbcTemplate.update(statement, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new ConflictException("Person already exists.");
        }

        Long id = keyHolder.getKey().longValue();
        person.setId(id);
        return id;
    }

    public Long update(Long id, Person person) {
        PreparedStatementCreator statement = c -> {
            PreparedStatement ps = c.prepareStatement(UPDATE_BY_ID);
            ps.setString(1, person.getFirstName());
            ps.setString(2, person.getLastName());
            ps.setString(3, person.getEmail());
            ps.setString(4, person.getBirthDate().toString());
            ps.setLong(5, id);
            return ps;
        };

        jdbcTemplate.update(statement);

        return id;
    }

    public void delete(Long id) {
        jdbcTemplate.update(LOGICAL_DELETE, id);
    }
}
