package uk.ac.warwick.dcs.sherlock.module.web.controllers.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import uk.ac.warwick.dcs.sherlock.module.web.configurations.SecurityConfig;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.AccountNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.AccountOwner;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Role;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.AccountForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.forms.PasswordForm;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.AccountWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.AccountRepository;
import uk.ac.warwick.dcs.sherlock.module.web.repositories.RoleRepository;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class ManageAdminController {
    @Autowired
	private AccountRepository accountRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private RoleRepository roleRepository;

    public ManageAdminController() { }

	@GetMapping("/admin/manage/{pathid}")
	public String manageGet(
			@ModelAttribute("subAccount") AccountWrapper subAccount,
			Model model
	) {
		model.addAttribute("accountForm", new AccountForm(subAccount.getAccount()));
		return "settings/admin/manage";
	}

	@PostMapping("/admin/manage/{pathid}")
	public String managePost(
			@Valid @ModelAttribute AccountForm accountForm,
			BindingResult result,
			@ModelAttribute("subAccount") AccountWrapper subAccount,
			@ModelAttribute("account") AccountWrapper account,
			Model model
	) {
		if (!result.hasErrors()) {
		    //Check that they are attempting to change the email
		    if (!accountForm.getEmail().equals(subAccount.getEmail())) {
		        //if attempting to change, check that the email doesn't already exist
                if (accountRepository.findByEmail(accountForm.getEmail()) != null) {
                    result.reject("error_email_exists");
                    return "settings/admin/manage";
                }
            }

            subAccount.getAccount().setEmail(accountForm.getEmail());
            subAccount.getAccount().setUsername(accountForm.getName());
            accountRepository.save(subAccount.getAccount());

            boolean isAdmin = false; //whether or not the user is already an admin
            Role adminRole = null; //the admin role object for that account

            for (Role role : subAccount.getRoles()) {
                if (role.getName().equals("ADMIN")) {
                    isAdmin = true;
                    adminRole = role;
                }
            }

            if (accountForm.isAdmin() && !isAdmin) { //Add admin role
                roleRepository.save(new Role("ADMIN", subAccount.getAccount()));
            }

            if (!accountForm.isAdmin() && isAdmin) { //Remove admin role
                roleRepository.delete(adminRole);
            }

            accountForm.setOldPassword("");
            model.addAttribute("success_msg", "admin_account_updated_details");
		}

		return "settings/admin/manage";
	}

	@GetMapping("/admin/password/{pathid}")
	public String passwordGet(Model model) {
		model.addAttribute("passwordForm", new PasswordForm());
		return "settings/admin/password";
	}

	@PostMapping("/admin/password/{pathid}")
	public String passwordPost(
			@Valid @ModelAttribute PasswordForm passwordForm,
			BindingResult result,
			@ModelAttribute("subAccount") AccountWrapper subAccount,
			@ModelAttribute("account") AccountWrapper account,
			Model model
	) {
		if (!result.hasErrors()) {
            //Generate a random password
            String newPassword = SecurityConfig.generateRandomPassword();

            subAccount.getAccount().setPassword(bCryptPasswordEncoder.encode(newPassword));
            accountRepository.save(subAccount.getAccount());

            model.addAttribute("success_msg", "admin_accounts_change_password_confirm");
            model.addAttribute("newPassword", newPassword);
            return "settings/admin/passwordSuccess";
		}

		return "settings/admin/password";
	}

	@GetMapping("/admin/delete/{pathid}")
	public String deleteGet(Model model) {
		model.addAttribute("passwordForm", new PasswordForm());
		return "settings/admin/delete";
	}

	@PostMapping("/admin/delete/{pathid}")
	public String deletePost(
			@Valid @ModelAttribute PasswordForm passwordForm,
			BindingResult result,
			@ModelAttribute("subAccount") AccountWrapper subAccount,
			@ModelAttribute("account") AccountWrapper account,
			Model model
	) {
		if (!result.hasErrors()) {
            accountRepository.delete(subAccount.getAccount());
            return "redirect:/admin?msg=deleted";
		}

		return "settings/admin/delete";
	}

	@ModelAttribute("subAccount")
	public AccountWrapper getAccount(
            @ModelAttribute("account") AccountWrapper account,
            @PathVariable(value="pathid") long pathid,
            Model model)
        throws AccountOwner, AccountNotFound
    {
    	Optional<Account> optional = accountRepository.findById(pathid);

    	if (!optional.isPresent()) {
    		throw new AccountNotFound("Account not found");
		}

    	AccountWrapper subAccount = new AccountWrapper(optional.get());

    	if (subAccount.getId() == account.getId()) {
    		throw new AccountOwner("You are not allowed to modify your own account.");
		}

        model.addAttribute("subAccount", subAccount);
		return subAccount;
	}
}