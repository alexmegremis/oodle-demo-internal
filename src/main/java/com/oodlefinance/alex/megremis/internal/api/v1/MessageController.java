package com.oodlefinance.alex.megremis.internal.api.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping ("/api/v1/message")
@Slf4j
public class MessageController {

    private       Integer          index    = 0;
    private final List<MessageDTO> messages = new ArrayList<>();

    public MessageController() {
        doSave("Hello world from internal startup");
    }

    @PostMapping
    public Integer save(@RequestBody final String message) {
        return doSave(message);
    }

    private Integer doSave(final String message) {
        index++;
        messages.add(MessageDTO.builder().id(index).message(message).build());

        log.info(">>> Saved {}", message);

        return index;
    }

    @GetMapping
    public List<MessageDTO> getAllMessages() {
        return messages;
    }

    @GetMapping (value = "{id}")
    @ResponseBody
    public ResponseEntity getMessage(@PathVariable (name = "id") final Integer id) {
        ResponseEntity result = null;

        Optional<MessageDTO> found = messages.stream().filter(m -> m.id == id).findAny();

        if (found.isPresent()) {
            result = ResponseEntity.ok(found.get());
        } else {
            result = ResponseEntity.notFound().build();
        }

        return result;
    }

    @DeleteMapping (value = "{id}")
    public ResponseEntity delete(@PathVariable (name = "id") final Integer id) {
        ResponseEntity result = null;

        Optional<MessageDTO> target = messages.stream().filter(m -> m.id == id).findAny();
        if (target.isPresent()) {
            synchronized (messages) {
                messages.remove(target.get());
            }
            result = ResponseEntity.ok().build();

            log.info(">>> Deleted {}", target.get());
        } else {
            result = ResponseEntity.notFound().build();
        }

        return result;
    }
}
