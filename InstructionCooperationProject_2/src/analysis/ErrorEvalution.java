package analysis;

public class ErrorEvalution {
	
	class Peak{
		int right, left, high;
		public Peak(){
			right=0;left=0;high=0;
		}
		public Peak(int right, int left, int high){
			this.right=right;this.left=left;this.high=high;
		}
	}
	
	public int evaluate(double[] data)
	{
		Peak[] peaks = new Peak[3];
		int idx=149,t=0,flag=0;
		// int ch1=0,ch2=0,ch3=0;
		while(idx>1)
		{
			Peak peak = new Peak();
			idx = findRight(peak, data, idx);
			// ch1=idx;
			if(idx>0)
			{
				idx=findHigh(peak, data, idx);
				// ch2=idx;
				if(idx>0)
				{
					idx = findLeft(peak, data, idx);
					// ch3 =idx;
					if(t<3)
					{
						if(peak.right-peak.left>5)
						{
							peaks[t]=peak;
							t++;
							//System.out.println(t+": r: "+ch1+" : "+data[ch1]+"\n"+t+": h: "+ch2+" : "+data[ch2]+"\n"+t+": l: "+ch3+" : "+data[ch3]);
						}
					}
					else
					{t=4;break;}
				}
			}
		}
		double min, max;
		min=max=data[149];
		for(int i=149; i>=0; i--)
		{
			if(data[i]<min)
				min=data[i];
			if(data[i]>max)
				max=data[i];
		}
		
		if(peaks[0]!=null&&peaks[0].high<100)
			flag=-3;
		if(t>4||t==0||min==0)
			flag=-2;
		else//if(peaks[0].right-peaks[0].high>peaks[0].high-peaks[0].left)
		{
			double slopeR = (data[peaks[0].high]-data[peaks[0].high+7])/7;
			double slopeL = (data[peaks[0].high]-data[peaks[0].high-7])/7;
			if(slopeL>slopeR)
				flag=-1;
			if(max-min<20)
				flag=-1;
		}
		//System.out.println(peaks[0].right);
		if(flag<0)
		{
			//if(peaks[0]!=null)
				//System.out.println(t+": r: "+peaks[0].right+" : "+data[peaks[0].right]+"\n"+t+": h: "+peaks[0].high+" : "+data[peaks[0].high]+"\n"+t+": l: "+peaks[0].left+" : "+data[peaks[0].left]);
			//else
				//System.out.println(t+": null");
		}
		return flag;
	}
	
	int findRight(Peak peak, double[] data, int idx){
		for(int i=idx; i>0; i--)
		{
			if(data[i]+0.7<=data[i-1]&&data[i]+1.4<=data[i-2])
			{
				peak.right=i;
				return i;
			}
		}
		return 0;
	}
	
	int findHigh(Peak peak, double[] data, int idx)
	{
		for(int i=idx; i>0; i--)
		{
			if(data[i]>data[i-1])
			{
				peak.high = i;
				return i;
			}
		}
		return 0;
	}
	
	int findLeft(Peak peak, double[] data, int idx)
	{
		for(int i=idx; i>0; i--)
		{
			if((data[i]-data[i-1]<0.7&&data[i]-data[i-2]<1.4))//||data[i]<data[peak.right])
			{
				peak.left = i;
				return i;
			}
		}
		return 0;
	}

}
