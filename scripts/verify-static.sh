#!/usr/bin/env sh
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$ROOT"

python3 - <<'PY'
from pathlib import Path
import xml.etree.ElementTree as ET
ET.parse('pom.xml')
version=Path('VERSION').read_text().strip()
pom=Path('pom.xml').read_text()
readme=Path('README.md').read_text()
changelog=Path('CHANGELOG.md').read_text()
assert f'<version>{version}</version>' in pom
assert f'Current release: **{version}**' in readme
assert f'## [{version}]' in changelog
license_text=Path('LICENSE').read_text().lower()
assert 'proprietary' in license_text and 'all rights reserved' in license_text
print('metadata checks: OK')
try:
    import yaml
except ImportError:
    print('YAML parser unavailable: skipped YAML parse')
else:
    for path in Path('.').rglob('*.yml'):
        yaml.safe_load(path.read_text())
    print('YAML parse: OK')
PY

OUT="${TMPDIR:-/tmp}/jamin8r-core-classes"
rm -rf "$OUT"
mkdir -p "$OUT"
javac --release 21 -d "$OUT" \
  src/main/java/com/jamin8r/eventing/IntegrationEvent.java \
  src/main/java/com/jamin8r/eventing/IntegrationEventPublisher.java \
  src/main/java/com/jamin8r/eventing/OutboxMessage.java \
  src/main/java/com/jamin8r/eventing/OutboxWriter.java \
  src/main/java/com/jamin8r/identity/UserDirectory.java \
  src/main/java/com/jamin8r/identity/UserNotFoundException.java \
  src/main/java/com/jamin8r/identity/UserRegistrationService.java \
  src/main/java/com/jamin8r/identity/UserSnapshot.java \
  src/main/java/com/jamin8r/identity/UsernameTakenException.java \
  src/main/java/com/jamin8r/identity/internal/UserRegisteredEvent.java \
  src/main/java/com/jamin8r/profile/ProfileNotFoundException.java \
  src/main/java/com/jamin8r/profile/ProfileService.java \
  src/main/java/com/jamin8r/profile/ProfileSnapshot.java \
  src/main/java/com/jamin8r/profile/internal/ProfileUpdatedEvent.java \
  src/main/java/com/jamin8r/social/FollowService.java \
  src/main/java/com/jamin8r/social/SelfFollowException.java \
  src/main/java/com/jamin8r/social/internal/UserFollowedEvent.java

echo "dependency-free Java compile: OK"


if [ -f MANIFEST.sha256 ]; then
  if command -v sha256sum >/dev/null 2>&1; then
    sha256sum -c MANIFEST.sha256 >/dev/null
  elif command -v shasum >/dev/null 2>&1; then
    shasum -a 256 -c MANIFEST.sha256 >/dev/null
  else
    echo "sha256sum or shasum unavailable: manifest verification skipped"
    exit 0
  fi
  echo "source manifest: OK"
fi
