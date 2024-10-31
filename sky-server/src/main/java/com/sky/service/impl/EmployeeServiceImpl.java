package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EditPasswordDTO;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus().equals(StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 保存员工信息
     *
     * @param employeeDTO 员工数据传输对象，包含要保存的员工信息
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        // 创建一个新的Employee对象
        Employee employee = new Employee();
        // 将员工数据传输对象的属性复制到员工对象中
        BeanUtils.copyProperties(employeeDTO, employee);
        // 设置员工状态为启用
        employee.setStatus(StatusConstant.ENABLE);
        // 设置员工初始密码为DEFAULT_PASSWORD，并使用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        // 设置员工创建时间为当前时间
        employee.setCreateTime(LocalDateTime.now());
        // 设置员工更新时间为当前时间
        employee.setUpdateTime(LocalDateTime.now());
        // 设置创建用户和更新用户的ID
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());
        // 将员工对象插入到数据库中
        employeeMapper.insert(employee);
    }


    /**
     * 根据条件分页查询员工信息
     * 该方法使用了PageHelper分页插件来实现分页查询，并返回包含总记录数和员工列表的PageResult对象
     *
     * @param employeePageQueryDTO 包含分页查询条件的DTO对象，包括页码和页面大小等信息
     * @return PageResult对象，包含查询结果的总记录数和员工列表
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // 启始分页，传入当前页码和页面大小
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        // 执行分页查询，获取包含分页信息的Page对象
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        // 获取查询结果的总记录数
        long total = page.getTotal();

        // 获取查询结果的员工列表
        List<Employee> records = page.getResult();

        // 构造并返回包含总记录数和员工列表的PageResult对象
        return new PageResult(total, records);
    }

    /**
     * 根据员工状态启动或停止员工账户
     * 此方法更新员工的状态，从而实现启动或停止账户的功能
     *
     * @param status 员工账户的新状态，1代表启动，0代表停止
     * @param id     员工的唯一标识符，用于确定需要更新的员工记录
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 创建一个Employee对象，仅包含需要更新的字段：id和status
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .build();
        // 调用Mapper的update方法更新数据库中的员工记录
        employeeMapper.update(employee);
    }

    /**
     * 根据员工ID获取员工信息，并隐藏员工密码
     *
     * @param id 员工ID，用于查询员工信息
     * @return 返回查询到的员工对象，其中密码字段被隐藏处理
     */
    @Override
    public Employee getById(Long id) {
        // 通过员工ID从数据库中获取员工信息
        Employee employee = employeeMapper.getById(id);

        // 隐藏员工密码，无论原始密码为何，统一设置为隐藏字符串
        employee.setPassword("****************");

        // 返回处理后的员工对象
        return employee;
    }
    /**
     * 更新员工信息
     * <p>
     * 该方法接收一个员工数据传输对象（EmployeeDTO），将其属性复制到一个员工对象（Employee）中，
     * 然后设置当前时间和当前用户ID作为更新信息，最后调用Mapper的update方法更新数据库中的员工记录
     *
     * @param employeeDTO 员工数据传输对象，包含要更新的员工信息
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        // 创建一个新的员工对象
        Employee employee = new Employee();
        // 将员工数据传输对象的属性复制到员工对象中
        BeanUtils.copyProperties(employeeDTO, employee);
        // 设置员工更新时间为当前时间
        employee.setUpdateTime(LocalDateTime.now());
        // 设置更新用户的ID
        employee.setUpdateUser(BaseContext.getCurrentId());
        // 调用Mapper的update方法更新数据库中的员工记录
        employeeMapper.update(employee);
    }
    @Override
    public void editPassword(EditPasswordDTO editPasswordDTO) {
        Long empId = editPasswordDTO.getEmpId();
        String oldPassword = editPasswordDTO.getOldPassword();
        String newPassword = editPasswordDTO.getNewPassword();
        employeeMapper.editPassword(empId, oldPassword, newPassword);
    }
}

