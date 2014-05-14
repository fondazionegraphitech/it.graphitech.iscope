package geometryFormat.dataFormats;

import geometryFormat.InternalFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractDataFormat
{
	public abstract String getFileExtension();
	public abstract String getFormatName();
	public abstract void saveTo( InternalFormat internalFormat, OutputStream os ) throws IOException;
	public abstract InternalFormat loadFrom( InputStream is ) throws Exception;
	
}
