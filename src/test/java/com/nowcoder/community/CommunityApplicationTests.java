package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Autowired
	private SensitiveFilter sensitiveFilter;

	@Test
	void contextLoads() {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}
	@Test
	public void testApplicationContext(){
		System.out.println(this.applicationContext);
		AlphaDao alphaDao=applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao);
		System.out.println(alphaDao.get());
		AlphaDao alphaDao1 = applicationContext.getBean("alphaDaoHibernate",AlphaDao.class);
		System.out.println(alphaDao1);
		System.out.println(alphaDao1.get());
	}
	@Test
	public void testSensitiveFilter(){
		String text="这里可以吸毒，可以看书，可以赌博";
		String f = sensitiveFilter.filter(text);
		System.out.println(f);
	}
}
