package rmartin.ctf.guesser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class WebController {

    @Autowired
    SuperSecureTokenManager tokenManager;

    @Value("${challenge.flag}")
    String flag;

    @GetMapping("/")
    public ModelAndView index(ModelAndView mv){
        mv.setViewName("index");
        return mv;
    }

    @GetMapping("/error")
    public ModelAndView error(ModelAndView mv){
        mv.setViewName("error");
        return mv;
    }

    @PostMapping("/check")
    public ModelAndView checkToken(@RequestParam int number, ModelAndView mv, HttpServletRequest request){
        boolean correct = tokenManager.isValidToken(request.getRemoteAddr(), number);
        if(correct){
            mv.addObject("flag", flag);
            mv.setViewName("correct");
        } else {
            var usedTokens = tokenManager.getUsedTokens(request.getRemoteAddr());
            mv.addObject("usedTokens", usedTokens);
            mv.setViewName("tryHarder");
        }
        return mv;
    }

    @PostMapping("/reset")
    public ModelAndView reset(HttpServletRequest request){
        tokenManager.resetState(request.getRemoteAddr());
        return new ModelAndView("index");
    }
}
