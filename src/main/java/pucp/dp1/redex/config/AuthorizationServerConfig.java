//package pucp.dp1.redex.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
//
//import pucp.dp1.redex.services.impl.login.UserService;
//
//
///**
// * Configures the authorization server.
// * The @EnableAuthorizationServer annotation is used to configure the OAuth 2.0 Authorization Server mechanism,
// * together with any @Beans that implement AuthorizationServerConfigurer (there is a handy adapter implementation with empty methods).
// */
//@Configuration
//@EnableAuthorizationServer
//public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
//	
//	@Autowired
//	private AuthenticationManager authenticationManager;
//	
//	@Autowired
//	private UserService customUserDetailsService;
//	
//	@Bean
//	public JwtAccessTokenConverter tokenEnhancer() {
//		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//		converter.setSigningKey(JwtConfig.RSA_PRIVADA);
//		converter.setVerifierKey(JwtConfig.RSA_PUBLICA);
//		return converter;
//	}
//	
//	@Bean
//	public JwtTokenStore tokenStore() {
//		return new JwtTokenStore(tokenEnhancer());
//	}
//	
//	@Override
//	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//		security.
//			checkTokenAccess("isAuthenticated()")
//			.tokenKeyAccess("permitAll()");
//	}
//	
//	@Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//	
//	@Override
//	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//		clients
//			.inMemory()
//			.withClient("redex") //permisos para la aplicación usuario
//			.authorizedGrantTypes("client_credentials","password","refresh_token")
//			.scopes("read","write","trust")
//			.accessTokenValiditySeconds(20000)
//			.secret(passwordEncoder().encode("redex")); //password
//	}
//
//	@Override
//	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//		endpoints
//			.authenticationManager(authenticationManager)
//			.tokenStore(tokenStore())
//			.accessTokenConverter(tokenEnhancer())
//			.userDetailsService(customUserDetailsService);
//	}
//}
