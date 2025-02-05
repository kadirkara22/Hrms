package kodlamaio.hrms.business.concretes;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;


import kodlamaio.hrms.business.abstracts.EmployerService;
import kodlamaio.hrms.business.abstracts.UserService;
import kodlamaio.hrms.core.business.BusinessEngine;
import kodlamaio.hrms.core.utilities.abstracts.EmailVerificationService;
import kodlamaio.hrms.core.utilities.results.DataResult;
import kodlamaio.hrms.core.utilities.results.ErrorDataResult;
import kodlamaio.hrms.core.utilities.results.ErrorResult;
import kodlamaio.hrms.core.utilities.results.Result;
import kodlamaio.hrms.core.utilities.results.SuccessDataResult;
import kodlamaio.hrms.core.utilities.results.SuccessResult;
import kodlamaio.hrms.dataAccess.abstracts.EmployerDao;
import kodlamaio.hrms.entities.concretes.Candidate;
import kodlamaio.hrms.entities.concretes.EmailVerification;
import kodlamaio.hrms.entities.concretes.Employer;
import kodlamaio.hrms.entities.concretes.HrmsVerification;
import kodlamaio.hrms.entities.concretes.User;

@Service
public class EmployerManager implements EmployerService{
	
	private EmployerDao employerDao;
	private EmailVerificationService emailVerificationService;
	private UserService userService;
	
	public EmployerManager(EmployerDao employerDao, EmailVerificationService emailVerificationService,
			UserService userService) {
		super();
		this.employerDao = employerDao;
		this.emailVerificationService = emailVerificationService;
		this.userService = userService;
	}

	@Override
	public DataResult<Employer> add(Employer employer) {
		// TODO Auto-generated method stub
		Result engine = BusinessEngine.run(
				companyNameChecker(employer),webSiteChecker(employer),
				passwordNullChecker(employer),
				isRealEmployer(employer),
				isRealPhoneNumber(employer),
				isEmailAlreadyRegistered(employer)
				);
		if(!engine.isSuccess()) {
			return new ErrorDataResult(null,engine.getMessage());
		}
		User savedUser = this.userService.add(employer);
		this.emailVerificationService.generateCode(new EmailVerification(),savedUser.getId());
		return new SuccessDataResult<Employer>(this.employerDao.save(employer),"İşveren hesabı eklendi, doğrulama kodu gönderildi ID:"+employer.getId());
	}
	
	private Result companyNameChecker(Employer employer) {
		if(employer.getCompanyName().isBlank() || employer.getCompanyName() == null) {
			return new ErrorResult("Şirket Adı Doldurulmak Zorundadır");
		}
		return new SuccessResult();
	}
	
	private Result webSiteChecker(Employer employer) {
		if(employer.getWebAddress().isBlank() || employer.getWebAddress() == null) {
			return new ErrorResult("WebSite Adresi Doldurulmak Zorundadır");
		}
		return new SuccessResult();
	}
	
	private Result isRealEmployer(Employer employer) {
		 String regex = "^(.+)@(.+)$";
	     Pattern pattern = Pattern.compile(regex);
	     Matcher matcher = pattern.matcher(employer.getEmail());
	     if(!matcher.matches()) {
	    	 return new ErrorResult("Geçersiz Email Adresi");
	     }
	     else if(!employer.getEmail().contains(employer.getWebAddress())) {
	    	 return new ErrorResult("Domain adresi girmek zorundasınız"); 
	     }
	 	return new SuccessResult();
	     
	}
	
	private Result isEmailAlreadyRegistered(Employer employer) {
		if(employerDao.findAllByEmail(employer.getEmail()).stream().count() != 0) {
			 return new ErrorResult("Email zaten kayıtlı"); 
		}
	 	return new SuccessResult();
	}
	
	private Result passwordNullChecker(Employer employer) {
		if(employer.getPassword().isBlank() || employer.getPassword() == null) {
			 return new ErrorResult("Şifre Bilgisi Doldurulmak zorundadır"); 
		}
		return new SuccessResult();
	}
	
	private Result isRealPhoneNumber(Employer employer) {
		String patterns 
	      = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$" 
	      + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$" 
	      + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
		
		
		Pattern pattern = Pattern.compile(patterns);
		Matcher matcher = pattern.matcher(employer.getPhoneNumber());
		if(!matcher.matches()) {
			 return new ErrorResult("Geçersiz Telefon Numarası"); 
		}
		return new SuccessResult();
		
	}

	@Override
	public DataResult<List<Employer>> getAll() {
		// TODO Auto-generated method stub
		return new SuccessDataResult<List<Employer>>(this.employerDao.findAll(),"Başarılı Şekilde Employer Listelendi");
	}

	@Override
	public Result delete(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result register(Employer employer, HrmsVerification hrmsVerification, EmailVerification emailVerification) {
		// TODO Auto-generated method stub
		return null;
	}

	

	
	
	

}
