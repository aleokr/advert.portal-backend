package com.app.advert.portal.mapper;

import com.app.advert.portal.model.Permission;
import com.app.advert.portal.model.Role;
import com.app.advert.portal.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT id, name, surname, email, login, company_id from USERS where id=#{id}")
    @Results(value = {
            @Result(property = "companyId", column = "company_id"),
            @Result(property = "roles", javaType = List.class, column = "id", many = @Many(select = "getRolesAndPermissionsByUserId"))})
    User getById(Long id);

    @Select("SELECT id, name, surname, email, login, password, company_id from USERS where login=#{username}")
    @Results(value = {
            @Result(property = "companyId", column = "company_id"),
            @Result(property = "roles", javaType = List.class, column = "id", many = @Many(select = "getRolesAndPermissionsByUserId"))})
    User getByUsername(String username);

    @Select("SELECT r.id, r.name FROM ROLES r JOIN USER_ROLE ur ON ur.role_id = r.id WHERE ur.user_id=#{id}")
    @Results(value = {
            @Result(property = "permissions", javaType = List.class, column = "id", many = @Many(select = "getPermissionByRoleId"))})
    List<Role> getRolesAndPermissionsByUserId(Long id);

    @Insert("INSERT INTO USERS(name, surname, email, login, password, created_at) values (#{name},#{surname},#{email},#{login},#{password}, now())")
    void saveUser(User user);

    @Update("UPDATE USERS SET name = #{name}, surname = #{surname}, email = #{email} where id = #{id}")
    void updateUser(User user);

    @Insert("INSERT INTO USER_ROLE(role_id, user_id) values ((SELECT id FROM ROLES WHERE name = #{roleName}),(SELECT id FROM USERS WHERE login = #{username}))")
    void addRoleToUser(String roleName, String username);

    @Delete("DELETE FROM USER_ROLE WHERE user_id = #{userId}")
    void deleteUserRoles(Long userId);

    @Delete("DELETE FROM USERS WHERE id = #{userId}")
    void deleteUserById(Long userId);

    @Select("SELECT p.id, p.name FROM PERMISSIONS p JOIN ROLE_PERMISSION rp ON rp.permission_id = p.id WHERE rp.role_id=#{id}")
    Permission getPermissionByRoleId(Long id);

    @Update("UPDATE USERS SET company_id = #{companyId} where id = #{userId}")
    void addCompanyToUser(Long userId, Long companyId);

    @Update("UPDATE USERS SET company_id = null where company_id = #{companyId}")
    void deleteCompanyFromUsers(Long companyId);
}
