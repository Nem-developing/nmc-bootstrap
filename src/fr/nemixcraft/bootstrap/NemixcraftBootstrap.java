package fr.nemixcraft.bootstrap;

import static fr.theshark34.swinger.Swinger.getResource;
import static fr.theshark34.swinger.Swinger.setResourcePath;

import java.io.File;
import java.io.IOException;

import fr.theshark34.openlauncherlib.bootstrap.Bootstrap;
import fr.theshark34.openlauncherlib.bootstrap.LauncherClasspath;
import fr.theshark34.openlauncherlib.bootstrap.LauncherInfos;
import fr.theshark34.openlauncherlib.util.ErrorUtil;
import fr.theshark34.openlauncherlib.util.GameDir;
import fr.theshark34.openlauncherlib.util.SplashScreen;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;

public class NemixcraftBootstrap {
	
	
	private static SplashScreen splash;
	private static SColoredBar bar;
	private static Thread barThread;
	
	private static final LauncherInfos SC_B_INFOS = new LauncherInfos("Nemixcraft", "fr.nemixcraft.launcher.LauncherFrame");
	private static final File SC_DIR = GameDir.createGameDir("Nemixcraft");
	private static final LauncherClasspath SC_B_CP = new LauncherClasspath(new File(SC_DIR, "Launcher/launcher.jar"), new File(SC_DIR, "Launcher/Libs/"));
	private static ErrorUtil errorUtil = new ErrorUtil(new File(SC_DIR, "Launcher/CRASH"));

	
	public static void main(String[] args) {
		setResourcePath("/fr/nemixcraft/bootstrap/resources/");
		displaySplash();
		try {
			doUpdate();
		} catch (Exception e) {
			errorUtil.catchError(e, "Impossible de mettre a jour le launcher Nemixcraft");
			barThread.interrupt();
		}
		try {
			launcherLauncher();	
		} catch (IOException e){
			errorUtil.catchError(e, "Impossible de lancer le launcher");
		}
	}
	
	private static void displaySplash() {
		splash = new SplashScreen("Nemixcraft", getResource("icon.png"));
		splash.setLayout(null);
		bar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
		bar.setBounds(4, 271, 386, 9);
		splash.add(bar);
		bar.setVisible(true);
		splash.setVisible(true);
		
		
	}
	
	private static void doUpdate() throws Exception {
		SUpdate su = new SUpdate("https://nemixcraft.com/minecraft/bootstrap/", new File(SC_DIR, "Launcher"));
		su.getServerRequester().setRewriteEnabled(true);
		su.addApplication(new FileDeleter());
		
		barThread = new Thread() {
			@Override
			public void run() {
				while(!this.isInterrupted()) {
					bar.setValue((int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000));
					bar.setMaximum((int) BarAPI.getNumberOfTotalBytesToDownload() / 1000);
				}
			}
		};
		barThread.start();
		su.start();
		barThread.interrupt();
	}
	
	private static void launcherLauncher() throws IOException {
		Bootstrap bootstap = new Bootstrap(SC_B_CP, SC_B_INFOS);
		Process p = bootstap.launch();
		splash.setVisible(false);
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			
		}
		System.exit(0);
		
	}

}
