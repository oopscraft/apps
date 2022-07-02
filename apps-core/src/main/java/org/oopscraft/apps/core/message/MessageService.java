package org.oopscraft.apps.core.message;

import lombok.RequiredArgsConstructor;
import org.oopscraft.apps.core.data.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

	private final MessageRepository messageRepository;

	/**
	 * Returns messages
	 * @param messageSearch messageSearch
	 * @param pageRequest pageRequest
	 * @return
	 * @throws Exception
	 */
    public List<Message> getMessages(MessageSearch messageSearch, PageRequest pageRequest) throws Exception {

    	// where clause
		Specification<Message> specification = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if(messageSearch.getId() != null) {
				predicates.add(builder.like(root.get(Message_.ID), '%' + messageSearch.getId() + '%'));
			}
			if(messageSearch.getName() != null) {
				predicates.add(builder.like(root.get(Message_.NAME), '%' + messageSearch.getName() + '%'));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};

		// sort clause
		Sort sort = Sort.by(Order.asc(Message_.SYSTEM_DATA).nullsLast(), Order.asc(Message_.ID));

		// retrieves
		Page<Message> messagesPage = messageRepository.findAll(specification, pageRequest);
		pageRequest.setTotalCount(messagesPage.getTotalElements());
		return messagesPage.getContent();
    }

    /**
     * Returns message
     * @param message
     * @return
     * @throws Exception
     */
    public Message getMessage(Message message) throws Exception {
    	return getMessage(message.getId());
    }
    
    /**
     * getMessage
     * @param id
     * @return
     * @throws Exception
     */
    public Message getMessage(String id) throws Exception {
    	return messageRepository.findById(id).orElse(null); 
    }

    /**
     * Saves message
     * @param message
     * @throws Exception
     */
    public void saveMessage(Message message) {
    	Message one = messageRepository.findById(Optional.ofNullable(message.getId()).orElse("")).orElse(null);
    	if(one == null) {
    		one = new Message();
    		one.setId(message.getId());
    	}
    	one.setName(message.getName());
    	one.setValue(message.getValue());
    	one.setNote(message.getNote());
    	messageRepository.saveAndFlush(one);
    }

    /**
     * Deletes message
     * @param message
     * @throws Exception
     */
    public void deleteMessage(Message message) {
        messageRepository.delete(message);
    }

}
