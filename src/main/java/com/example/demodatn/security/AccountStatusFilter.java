package com.example.demodatn.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class AccountStatusFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImpl users;
    public AccountStatusFilter(UserDetailsServiceImpl users) { this.users=users; }
    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)
            throws ServletException,IOException {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null && auth.getPrincipal() instanceof CustomUserDetails) {
            boolean enabled;
            try { enabled=users.loadUserByUsername(auth.getName()).isEnabled(); }
            catch(UsernameNotFoundException ex) { enabled=false; }
            if(!enabled) {
                SecurityContextHolder.clearContext();
                HttpSession session=request.getSession(false);
                if(session!=null)session.invalidate();
                response.sendRedirect(request.getContextPath()+"/login?disabled");
                return;
            }
        }
        chain.doFilter(request,response);
    }
}
