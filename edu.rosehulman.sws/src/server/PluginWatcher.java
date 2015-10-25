/*
 * PluginWatcher.java
 * Oct 22, 2015
 *
 * Simple Web Server (SWS) for EE407/507 and CS455/555
 * 
 * Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 * Contact Us:
 * Chandan Raj Rupakheti (rupakhcr@clarkson.edu)
 * Department of Electrical and Computer Engineering
 * Clarkson University
 * Potsdam
 * NY 13699-5722
 * http://clarkson.edu/~rupakhcr
 */
 
package server;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class PluginWatcher implements Runnable{

	
	private String dir;
	private Server server;

	public PluginWatcher(Server server, String dir){
		this.server = server;
		this.dir = dir;
	}

	@Override
	public void run() {
		Path dir = Paths.get(this.dir);
		WatchService watcher;
		try {
			watcher = FileSystems.getDefault().newWatchService();
			dir.register(watcher,
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);
			  while (true) {
		            WatchKey watchKey;
		            try {
						watchKey = watcher.take();
					} catch (InterruptedException e) {
						continue;
					}
		      
		            for (WatchEvent<?> event: watchKey.pollEvents()) {
		                WatchEvent.Kind<?> kind = event.kind();

		                // This key is registered only
		                // for ENTRY_CREATE events,
		                // but an OVERFLOW event can
		                // occur regardless if events
		                // are lost or discarded.
		                if (kind == StandardWatchEventKinds.OVERFLOW) {
		                    continue;
		                }

		            WatchEvent<Path> ev = (WatchEvent<Path>) event;
		            Path filename = ev.context();
		            if(kind == StandardWatchEventKinds.ENTRY_DELETE){
		            	server.removePlugin(filename);
		            } else {
		            	server.uploadPlugin(filename);
		            }
		          }
		            watchKey.reset();
			  }
		} catch (IOException x) {
			System.err.println(x);
		}
			
	}

}
