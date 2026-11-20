from pathlib import Path
import os, subprocess, tempfile, json, time, shutil
import argparse
parser=argparse.ArgumentParser(description='Test survival mining and teleportation in an offscreen Horizon client.')
parser.add_argument('--client',type=Path,required=True)
parser.add_argument('--control',type=Path,required=True)
parser.add_argument('--resources',type=Path,required=True)
parser.add_argument('--pack-zip',type=Path,required=True)
args=parser.parse_args()
client=args.client.resolve()
control=args.control.resolve()
with tempfile.TemporaryDirectory(prefix='undertow-input-') as tmp:
 lab=Path(tmp)
 packs=lab/'saves/testworld/datapacks'
 packs.mkdir(parents=True)
 shutil.copy2(args.pack_zip.resolve(),packs)
 env=dict(os.environ,HORIZON_HOME=str(lab/'home'),HORIZON_LAUNCHER_HOME=str(lab/'launcher'),HORIZON_LAUNCHER_NO_SELF_UPDATE='1',XDG_DATA_HOME=str(lab/'data'),XDG_CONFIG_HOME=str(lab/'config'),HORIZON_NO_AUDIO='1')
 socket=lab/'control.sock'
 log=open(lab/'game.log','w')
 game=subprocess.Popen([client,'--resources',args.resources.resolve(),'--world','testworld','--creative','--offscreen','--seed','23','--radius','3','--width','480','--height','320','--control-socket',socket],cwd=lab,env=env,stdout=log,stderr=log)
 def ctl(*args):
  proc=subprocess.run([control,'--socket',socket,'--timeout','45',*args],cwd=lab,env=env,capture_output=True,text=True,timeout=55)
  if proc.returncode: raise RuntimeError((args,proc.stdout,proc.stderr))
  return json.loads(proc.stdout)
 try:
  for _ in range(200):
   if game.poll() is not None: raise RuntimeError((lab/'game.log').read_text(errors='replace')[-2000:])
   if socket.exists():break
   time.sleep(0.5)
  ctl('wait-ready','45')
  for c in ['gamerule spawn_mobs false','tp @s 8.5 8 8.5 0 0','fill -10 6 -10 26 14 26 stone','fill -9 7 -9 25 12 25 air','tp @s 8.5 7 8.5 0 0','setblock 8 8 11 stone','give @s diamond_pickaxe','gamemode survival','scoreboard players set #odds eu.roll 1','summon enderman 8.5 7 7.2 {NoAI:1b,PersistenceRequired:1b}']:
   ctl('command',c)
  ctl('select','minecraft:diamond_pickaxe')
  mined=ctl('break','8','8','11','--timeout','12')
  assert mined['ok'] and mined['drops'].get('minecraft:cobblestone')==1, mined
  ctl('wait','20')
  print('block',ctl('block','8','8','11'),flush=True)
  ctl('command','scoreboard players get @s eu.cool')
  result=ctl('messages','5')
  assert any('[eu.cool]' in line and ' has ' in line for line in result['chat']), result['chat']
  player=ctl('state')['player']
  x,y,z=player['position']
  assert (x-8.5)**2 + (z-8.5)**2 > 36 and player['air']==300 and player['health']==20, player
  print('Mining input passed. Dry arrival:',player['position'],'Health:',player['health'],'Air:',player['air'])
 finally:
  if game.poll() is None:
   try: ctl('quit');game.wait(timeout=8)
   except Exception: game.kill();game.wait()
  log.close()
