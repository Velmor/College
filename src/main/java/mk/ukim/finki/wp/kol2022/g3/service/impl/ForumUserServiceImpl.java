package mk.ukim.finki.wp.kol2022.g3.service.impl;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.ForumUserType;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidForumUserIdException;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidInterestIdException;
import mk.ukim.finki.wp.kol2022.g3.repository.ForumUserRepository;
import mk.ukim.finki.wp.kol2022.g3.repository.InterestRepository;
import mk.ukim.finki.wp.kol2022.g3.service.ForumUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ForumUserServiceImpl implements ForumUserService {

    private final ForumUserRepository forumUserRepository;
    private final InterestRepository interestRepository;

    public ForumUserServiceImpl(ForumUserRepository forumUserRepository, InterestRepository interestRepository)
    {
        this.forumUserRepository = forumUserRepository;
        this.interestRepository = interestRepository;
    }


    @Override
    public List<ForumUser> listAll()
    {
        return this.forumUserRepository.findAll();
    }

    @Override
    public ForumUser findById(Long id)
    {
        return this.forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
    }

    @Override
    public ForumUser create(String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday)
    {
        List<Interest> interests=this.interestRepository.findAllById(interestId);
        return this.forumUserRepository.save(new ForumUser(name,email,password,type,interests,birthday));
    }

    @Override
    public ForumUser update(Long id, String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday)
    {
        List<Interest> interests=this.interestRepository.findAllById(interestId);
        ForumUser fu=forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
        fu.setName(name);
        fu.setEmail(email);
        fu.setPassword(password);
        fu.setType(type);
        fu.setInterests(interests);
        fu.setBirthday(birthday);

        return this.forumUserRepository.save(fu);
    }

    @Override
    public ForumUser delete(Long id)
    {
        ForumUser fu=forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
        forumUserRepository.delete(fu);
        return fu;
    }

    @Override
    public List<ForumUser> filter(Long interestId, Integer age)
    {

        if (interestId != null && age !=null){
            Interest interest=interestRepository.findById(interestId).orElseThrow(InvalidInterestIdException::new);
            LocalDate date= LocalDate.now().minusYears(age);
            return forumUserRepository.findAllByInterestsContainsAndBirthdayBefore(interest,date);
        }
        else if(interestId != null){
            Interest interest=interestRepository.findById(interestId).orElseThrow(InvalidInterestIdException::new);
            return forumUserRepository.findAllByInterestsContains(interest);
        }
        else if(age !=null){
            LocalDate date= LocalDate.now().minusYears(age);
            return forumUserRepository.findAllByBirthdayBefore(date);
        }
        else {
            return forumUserRepository.findAll();
        }
    }
}
