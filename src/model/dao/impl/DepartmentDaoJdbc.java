package model.dao.impl;

import db.Db;
import db.DbException;
import db.DbIntegrityException;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJdbc implements DepartmentDao {

    private Connection conn;

    public DepartmentDaoJdbc(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Department department) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("insert into department(name) values(?)", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, department.getName());
            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    department.setId(rs.getInt(1));
                }
                Db.closeResultSet(rs);
            } else {
                throw new DbException("Unexpected error! No rows affected!");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            Db.closeStatement(st);
        }
    }

    @Override
    public void update(Department department) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("update department set name = ? where id = ?");
            st.setString(1, department.getName());
            st.setInt(2, department.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            Db.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("delete from department where id = ?");
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbIntegrityException(e.getMessage());
        } finally {
            Db.closeStatement(st);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement("select * from department where id = ?");
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                return initiateDepartment(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            Db.closeStatement(st);
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement("select * from department");
            rs = st.executeQuery();

            List<Department> list = new ArrayList<>();
            while (rs.next()) {
                list.add(initiateDepartment(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            Db.closeStatement(st);
        }
    }

    private Department initiateDepartment(ResultSet rs) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt("Id"));
        department.setName(rs.getString("Name"));
        return department;
    }
}
