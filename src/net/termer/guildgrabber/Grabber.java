package net.termer.guildgrabber;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;

public class Grabber {
	public static JDA jda = null;
	public static File dir = null;
	
	private static int _channelTotal = -1;
	private static int _channelsComplete = 0;
	
	public static void main(String[] args) throws IOException, LoginException, InterruptedException {
		if(args.length > 0) {
			System.out.println("Setting up file system...");
			File tf = new File("token.ini");
			if(!tf.exists()) {
				tf.createNewFile();
				System.out.println("token.ini created. Please paste your user token into it, then start.");
				System.exit(0);
			}
			System.out.println("Fetching token...");
			String token = "";
			FileInputStream fin = new FileInputStream(tf);
			try {
				while(fin.available()>0) {
					token+=(char)fin.read();
				}
				fin.close();
			} catch(Exception e) {
				System.out.println("Error while reading token.ini");
				e.printStackTrace();
			}
			System.out.println("Logging in...");
			jda = new JDABuilder(AccountType.CLIENT).setToken(token).build().awaitReady();
			System.out.println("Logged in!");
			System.out.println("Archiving...");
			try {
				// Get guild
				Guild guild = jda.getGuildById(args[0]);
				
				// Create directory
				dir = new File(filter(guild.getName()));
				dir.mkdirs();
				
				// Retrieve total channels
				_channelTotal = guild.getTextChannels().size();
				
				for(TextChannel ch : guild.getTextChannels()) {
					File chfile = new File(filter(guild.getName())+"/"+ch.getName()+".txt");
					if(!chfile.exists()) {
						chfile.createNewFile();
					}
					new ChannelSaver(ch).start();
					System.out.println("Now archiving channel \""+ch.getName()+"\"...");
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No guild/server specified."
							 +" Please specify the desired guild/server's ID as an argument."
							 +" To get a guild/server's ID, simply right click its icon and click \"Copy ID\".");
		}
	}
	
	private static char[] acceptableChars = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','3','4','5','6','7','8','9','_', ' '};
	private static String filter(String str) {
		String result = "";
		for(char ch : str.toLowerCase().toCharArray()) {
			for(char acceptableChar : acceptableChars) {
				if(acceptableChar == ch) {
					result+=ch;
					break;
				}
			}
		}
		return result;
	}
	
	private static class ChannelSaver extends Thread {
		private TextChannel ch = null;
		
		public ChannelSaver(TextChannel channel) {
			ch = channel;
		}
		
		public void run() {
			int last = -1;
			MessageHistory hist = ch.getHistory();
			while(hist.size()!=last) {
				last = hist.size();
				hist.retrievePast(100).complete();
			}
			System.out.println("Channel \""+ch.getName()+"\" has "+Integer.toString(hist.size())+" messages.");
			File chfile = new File(filter(ch.getGuild().getName())+"/"+ch.getName()+".txt");
			try {
				FileOutputStream fout = new FileOutputStream(chfile);
				for(Message m : hist.getRetrievedHistory()) {
					write(m.getCreationTime().toString()+" "+m.getAuthor().getName()+" : "+m.getContentDisplay()+"\n",fout);
					if(m.getAttachments().size()>0) {
						write("With attachments:\n",fout);
						for(Attachment att : m.getAttachments()) {
							write(att.getFileName()+" ("+att.getUrl()+")",fout);
						}
					}
				}
				fout.close();
				_channelsComplete++;
				System.out.printf(
					"Channel \"%s\" archived! (%s/%s)",
					ch.getName(),
					_channelsComplete, _channelTotal
				);
			} catch (IOException e) {
				System.err.println("Failed to archive channel \""+ch.getName()+"\":");
				e.printStackTrace();
			}
		}
		
		private void write(String str, FileOutputStream out) throws IOException {
			for(int i = 0; i < str.length(); i++) {
				out.write((int)str.charAt(i));
			}
		}
	}
}
