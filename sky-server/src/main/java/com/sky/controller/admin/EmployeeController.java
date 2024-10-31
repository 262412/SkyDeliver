package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(value = "员工相关接口", tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO 包含登录信息的数据传输对象
     * @return 登录结果，包含用户信息和令牌
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录", notes = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        // 记录员工登录信息
        log.info("员工登录：{}", employeeLoginDTO);

        // 调用服务层方法处理登录逻辑
        Employee employee = employeeService.login(employeeLoginDTO);

        // 登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        // 构建返回的员工登录视图对象
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        // 返回登录成功结果
        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工退出", notes = "员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     *
     * @param employeeDTO 员工信息
     * @return 新增结果
     */
    @PostMapping
    @ApiOperation(value = "新增员工", notes = "新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工，员工数据：{}", employeeDTO.toString());
        employeeService.save(employeeDTO);
        return Result.success();
    }
    /**
     * 处理员工信息分页查询的GET请求
     * 该方法使用EmployeePageQueryDTO作为参数，用于传递分页查询的条件
     * @param employeePageQueryDTO 包含分页查询条件的DTO对象
     * @return 返回一个Result对象，其中包含分页查询的结果PageResult
     */
    @GetMapping(value = "/page")
    @ApiOperation(value = "员工分页查询", notes = "员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        // 记录分页查询的日志，以便于调试和审计
        log.info("员工分页查询，{}", employeePageQueryDTO);
        // 调用服务层的分页查询方法，并返回查询结果
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        // 返回一个表示成功的Result对象，其中包含分页查询结果
        return Result.success(pageResult);
    }
}
