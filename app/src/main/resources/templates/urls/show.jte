@import hexlet.code.dto.UrlsCheckPage
@param UrlsCheckPage page

@template.layout.page(
page = page,
content = @`
    <h1>Просмотр URL</h1>
    <table class="table table-bordered" data-test="url">
        <tbody>
        <tr><td>ID</td><td>${page.getUrl().getId()}</td></tr>
        <tr><td>Имя</td><td>${page.getUrl().getName()}</td></tr>
        <tr><td>Дата создания</td><td>${page.getUrl().getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</td></tr>
        </tbody>
    </table>

    <h2>Запустить проверку</h2>
    <form method="post" action="/urls/${page.getUrl().getId()}/checks">
        <input type="submit" value="Запустить проверку" class="btn btn-primary">
    </form>

    <h2>Проверки</h2>
    <table class="table table-bordered" data-test="checks">
        <thead>
        <tr>
            <th>ID</th>
            <th>Код ответа</th>
            <th>h1</th>
            <th>title</th>
            <th>description</th>
            <th>Дата создания</th>
        </tr>
        </thead>
        <tbody>
        @if(page.getChecks().isEmpty())
            <tr><td colspan="6" class="text-center">Нет проверок</td></tr>
        @else
            @for(var check : page.getChecks())
                <tr>
                    <td>${check.getId()}</td>
                    <td>${check.getStatusCode()}</td>
                    <td>${check.getH1()}</td>
                    <td>${check.getTitle()}</td>
                    <td>${check.getDescription()}</td>
                    <td>${check.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</td>
                </tr>
            @endfor
        @endif
        </tbody>
    </table>
`
)