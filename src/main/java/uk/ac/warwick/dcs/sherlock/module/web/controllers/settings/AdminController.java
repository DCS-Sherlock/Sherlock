package uk.ac.warwick.dcs.sherlock.module.web.controllers.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.warwick.dcs.sherlock.module.web.configurations.SecurityConfig;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.NotAjaxRequest;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Role;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.AccountForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.AccountRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.RoleRepository;

import javax.validation.Valid;

@Controller
public class AdminController {

	@Autowired
	public AccountRepository accountRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private RoleRepository roleRepository;

	public AdminController() { }

	@RequestMapping("/admin")
	public String indexGet() {
		return "settings/admin/index";
	}

	@RequestMapping ("/admin/list")
	public String listGetFragment(
			@ModelAttribute("isAjax") boolean isAjax,
			Model model
	) throws NotAjaxRequest {
		if (!isAjax) throw new NotAjaxRequest("/admin");

		model.addAttribute("accounts", accountRepository.findAll());
		return "settings/admin/fragments/list";
	}

	@RequestMapping("/admin/add")
	public String addGet(Model model) {
		model.addAttribute("accountForm", new AccountForm());
		return "settings/admin/add";
	}

	@PostMapping("/admin/add")
	public String addPost(
			@ModelAttribute("isAjax") boolean isAjax,
			@ModelAttribute("account") AccountWrapper account,
			@Valid @ModelAttribute AccountForm accountForm,
			BindingResult result,
			Model model
	) {
		if (!result.hasErrors()) {
			if (accountRepository.findByEmail(accountForm.getEmail()) == null) {
                //Generate a random password
                String newPassword = SecurityConfig.generateRandomPassword();

                Account newAccount = new Account(
                        accountForm.getEmail(),
                        bCryptPasswordEncoder.encode(newPassword),
                        accountForm.getName()
                );

                accountRepository.save(newAccount);

                roleRepository.save(new Role("USER", newAccount));
                if (accountForm.isAdmin()) {
                    roleRepository.save(new Role("ADMIN", newAccount));
                }

                model.addAttribute("success_msg", "admin_account_new_start");
                model.addAttribute("newPassword", newPassword);
                return "settings/admin/passwordSuccess";
			} else {
				result.reject("error_email_exists");
			}
		}

		return "settings/admin/add";
	}

}
