package com.example.demo.controller;

import com.example.demo.dto.TodoItemDto;
import com.example.demo.service.TodoItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RequestMapping("/api/todos")
@RestController
public class TodoRestController {

    private TodoItemService todoService;

    public TodoRestController(TodoItemService todoService) {
        this.todoService = todoService;
    }

    /**
     * TodoItem 목록 조회
     *
     * @param pageable
     * @param id
     * @param keyword
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getTodoItemList(@PageableDefault(size = 5) Pageable pageable, @PathVariable(name = "id", required = false) Long id, @PathVariable(name = "keyword", required = false) String keyword) {

        try {
            if (id != null) {
                // 검색한 TodoItem 목록 조회
                return ResponseEntity.ok(todoService.getSearchTodoList(id, keyword));
            }

            // 전체 TodoItem 목록 조회
            return ResponseEntity.ok(todoService.getTodoList(pageable));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * TodoItem 상세 조회
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getTodoItem(@PathVariable("id") Long id) {
        return ResponseEntity.ok(todoService.getModifyTodoItem(id));
    }

    /**
     * TodoItem 추가
     *
     * @param todoItemDto
     * @param bindingResult
     * @return
     */
    @PostMapping
    public ResponseEntity<?> addTodoItem(@Valid @RequestBody TodoItemDto todoItemDto, BindingResult bindingResult) {

        // validation 체크
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }

        try {
            todoService.addTodoItem(todoItemDto);
            return new ResponseEntity<>("추가 되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * TodoITem 수정
     *
     * @param id
     * @param todoItemDto
     * @param bindingResult
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyTodoItem(@PathVariable("id") Long id, @Valid @RequestBody TodoItemDto todoItemDto, BindingResult bindingResult) {

        try {
            if (todoItemDto.getModifyType() == 1) {     // 완료 여부 체크(1) 인지 수정 버튼 클릭(0)인지
                return new ResponseEntity<>(todoService.checkTodoItem(id, todoItemDto), HttpStatus.OK);
            }

            // validation 체크
            if (bindingResult.hasErrors()) {
                String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
                return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(todoService.modifyTodoItem(id, todoItemDto), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * TodoItem 삭제
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable("id") Long id) {
        try {
            todoService.deleteTodoItem(id);
            return new ResponseEntity<>("삭제 되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
