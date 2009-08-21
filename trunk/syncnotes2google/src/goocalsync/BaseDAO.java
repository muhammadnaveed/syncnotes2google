package goocalsync;

import java.util.Calendar;

public interface BaseDAO {

	public String insert(BaseDoc entry);
	
	public void update(BaseDoc entry);
	
	public void delete(String id);
	
	public BaseDoc select(String id);
	
	public BaseDoc getFirstEntry();
	
	public BaseDoc getNextEntry();
	
	public String getDirection();
	
}
