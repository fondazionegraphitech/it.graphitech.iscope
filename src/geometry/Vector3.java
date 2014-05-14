package geometry;

/*
 * 
 * Vector3 Utility Class
 * Federico Devigili 2012
 * 
 */

public class Vector3 
{	
	public double x = 0.0f;
	public double y = 0.0f;
	public double z = 0.0f;
	
	public Vector3 ()
	{
	}
		
	public Vector3 (float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3 (double x, double y, double z)
	{
		this.x = (double)x;
		this.y = (double)y;
		this.z = (double)z;
	}
	
	public Vector3(double f)
	{
		this.x = f;
		this.y = f;
		this.z = f;
	}

	public double Magnitude()
	{
		return (double) Math.sqrt( x*x + y*y + z*z );
	}
	
	public double Distance( Vector3 that )
	{
		double dx = this.x - that.x;
		double dy = this.y - that.y;
		double dz = this.z - that.z;
		return (double) Math.sqrt( dx*dx + dy*dy + dz*dz );
	}
	
	public void Normalize()
	{
		double magnitude = this.Magnitude();
		this.x = this.x / magnitude; 
		this.y = this.y / magnitude; 
		this.z = this.z / magnitude; 
	}
	
	public Vector3 Normalized()
	{
		Vector3 result = new Vector3( );
		double magnitude = this.Magnitude();
		result.x = this.x / magnitude; 
		result.y = this.y / magnitude; 
		result.z = this.z / magnitude; 
		return result;
	}
	
	public Vector3 Cross( Vector3 that )
	{
		Vector3 result = new Vector3( );
		result.x = this.y * that.z - this.z * that.y; 
		result.y = this.z * that.x - this.x * that.z; 
		result.z = this.x * that.y - this.y * that.x; 
		return result;
	}
	
	public double Dot( Vector3 that )
	{
		return this.x * that.y + this.y * that.y + this.z * that.z; 
	}
	
	public Vector3 Sum( Vector3 that )
	{
		Vector3 result = new Vector3( );
		result.x = this.x + that.x; 
		result.y = this.y + that.y; 
		result.z = this.z + that.z; 
		return result;
	}
	
	public Vector3 Sub( Vector3 that )
	{
		Vector3 result = new Vector3( );
		result.x = this.x - that.x; 
		result.y = this.y - that.y; 
		result.z = this.z - that.z; 
		return result;
	}
	
	public Vector3 Mul( Vector3 that )
	{
		Vector3 result = new Vector3( );
		result.x = this.x * that.x; 
		result.y = this.y * that.y; 
		result.z = this.z * that.z; 
		return result;
	}
	
	public Vector3 Mul( double that )
	{
		Vector3 result = new Vector3( );
		result.x = this.x * that; 
		result.y = this.y * that; 
		result.z = this.z * that; 
		return result;
	}
	
	public Vector3 Div( Vector3 that )
	{
		Vector3 result = new Vector3( );
		result.x = this.x / that.x; 
		result.y = this.y / that.y; 
		result.z = this.z / that.z; 
		return result;
	}
	
	public String toString()
	{
		return "(" + x + ",\t" +y+",\t"+z+")";
	}
	
	public static Vector3 GetNormal( Vector3 a, Vector3 b, Vector3 c )
	{
		Vector3 x = a.Sub(b);
		Vector3 y = a.Sub(c);
		
		Vector3 n = x.Cross(y);
		n.Normalize();

		return n;
	}
}