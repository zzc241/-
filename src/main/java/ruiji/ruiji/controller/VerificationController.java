package ruiji.ruiji.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ruiji.ruiji.common.R;
import ruiji.ruiji.service.impl.EmailService;

@RestController
@RequestMapping("/api/verify")
public class VerificationController {
    
    @Autowired
    private EmailService emailService;
    
    @PostMapping("/send-code")
    public R sendVerificationCode(@RequestParam String email) {
        try {
            emailService.sendVerificationCode(email);
            return R.success("验证码发送成功");
        } catch (Exception e) {
            return R.error("验证码发送失败：" + e.getMessage());
        }
    }
    
    // @PostMapping("/send-phone-code")
    // public R sendPhoneVerificationCode(
    //         @RequestParam String phoneNumber,
    //         @RequestParam(defaultValue = "0") int carrierType) {
    //     try {
    //         emailService.sendVerificationCodeToPhone(phoneNumber, carrierType);
    //         return R.success("验证码发送成功");
    //     } catch (Exception e) {
    //         return R.error("验证码发送失败：" + e.getMessage());
    //     }
    // }
}
