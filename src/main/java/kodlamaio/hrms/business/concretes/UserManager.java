package kodlamaio.hrms.business.concretes;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kodlamaio.hrms.business.abstracts.UserService;
import kodlamaio.hrms.core.utilities.abstracts.EmailCheckService;
import kodlamaio.hrms.core.utilities.results.DataResult;
import kodlamaio.hrms.core.utilities.results.ErrorResult;
import kodlamaio.hrms.core.utilities.results.Result;
import kodlamaio.hrms.core.utilities.results.SuccessDataResult;
import kodlamaio.hrms.core.utilities.results.SuccessResult;
import kodlamaio.hrms.dataAccess.abstracts.UserDao;
import kodlamaio.hrms.entities.concretes.EmailVerification;
import kodlamaio.hrms.entities.concretes.HrmsVerification;
import kodlamaio.hrms.entities.concretes.User;

@Service
public class UserManager implements UserService{

	private UserDao userDao; 
	private EmailCheckService emailCheckService;
	
	@Autowired
	public UserManager(UserDao userDao,EmailCheckService emailCheckService) {
		super();
		this.userDao = userDao;
		this.emailCheckService = emailCheckService;
	}

	@Override
	public DataResult<List<User>>  getAll() {
		 
		return new SuccessDataResult<List<User>>(this.userDao.findAll(),"Data listelendi");
	}

	@Override
	public Result add(User user) {
	
		List<User> users=this.userDao.findAll();
		for (User user2 : users) {
			if (user2==user) {
				return new ErrorResult("kullanıcı zaten mevcut başka bir kullanıcı giriniz");
			} else {
                     this.userDao.save(user);               
			}
		}
		return new SuccessResult("Kullanıcı eklendi");
	}

	@Override
	public Result register(User user, HrmsVerification hrmsVerification, EmailVerification emailVerification) {
		Result result = new SuccessResult("Kayit basarili.");

		if (emailCheckService.emailIsItUsed(user.getEmail())) {
			result = new ErrorResult("Email sisteme kayitli.");
			return result;
		}if(emailVerification.isEmailBool() == false){
			result = new ErrorResult("Email onayi gerekiyor.");
			return result;
			
		}if(hrmsVerification.isHrmsBool() == false){
			result = new ErrorResult("Hrms onayi gerekiyor.");
			return result;
		}else {
			this.userDao.save(user);
			
		}
		return result;
	}

}
